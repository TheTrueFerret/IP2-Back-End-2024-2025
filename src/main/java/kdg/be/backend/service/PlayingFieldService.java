package kdg.be.backend.service;

import kdg.be.backend.controller.dto.requests.PlayerMoveTileDto;
import kdg.be.backend.controller.dto.requests.PlayerMoveTileSetDto;
import kdg.be.backend.domain.*;
import kdg.be.backend.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlayingFieldService {
    private final PlayingFieldRepository playingFieldRepository;
    private final TileSetRepository tileSetRepository;
    private final TileRepository tileRepository;
    private final DeckRepository deckRepository;
    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;
    private final MoveValidationService moveValidationService;
    private static final Logger log = LoggerFactory.getLogger(PlayingFieldService.class);

    public PlayingFieldService(PlayingFieldRepository playingFieldRepository, TileSetRepository tileSetRepository, TileRepository tileRepository, DeckRepository deckRepository, PlayerRepository playerRepository, GameRepository gameRepository, MoveValidationService moveValidationService) {
        this.playingFieldRepository = playingFieldRepository;
        this.tileSetRepository = tileSetRepository;
        this.tileRepository = tileRepository;
        this.deckRepository = deckRepository;
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.moveValidationService = moveValidationService;
    }

    public List<TileSet> getPlayingFieldByGameId(UUID gameId) {
        List<TileSet> tileSets = tileSetRepository.findTileSetsByGameId(gameId);
        for (TileSet tileSet : tileSets) {
            tileSet.setTiles(new HashSet<>(tileRepository.findTilesByTileSetId(tileSet.getId())));
        }
        if (tileSets.isEmpty()) {
            throw new IllegalArgumentException("No TileSets found for Game ID: " + gameId);
        }
        return tileSets;
    }

    @Transactional
    public void handlePlayerMoves(UUID gameId, List<PlayerMoveTileSetDto> playerMoveTileSetDtos) {
        List<TileSet> existingTileSets = tileSetRepository.findTileSetsByGameId(gameId);
        for (PlayerMoveTileSetDto playerMoveTileSetDto : playerMoveTileSetDtos) {
            // Extract tile IDs from the DTO
            List<UUID> tileIds = playerMoveTileSetDto.tiles().stream()
                    .map(PlayerMoveTileDto::id)
                    .collect(Collectors.toList());

            // Retrieve the tiles from the database
            List<Tile> tiles = tileRepository.findAllByIdWithTileSetAndTiles(tileIds);
            if (tiles.size() != tileIds.size()) {
                throw new IllegalArgumentException("The tiles from the dto don't exist");
            }

            TileSet tileSet = playerMoveTileSetDto.id() == null
                    ? createNewTileSet(gameId)
                    : tileSetRepository.findByIdWithTiles(playerMoveTileSetDto.id())
                    .orElseThrow(() -> new IllegalArgumentException("TileSet not found"));


            // Set TileSet properties
            tileSet.setGridRow(playerMoveTileSetDto.gridRow());
            tileSet.setStartCoordinate(playerMoveTileSetDto.startCoord());
            tileSet.setEndCoordinate(playerMoveTileSetDto.endCoord());


            // Update tile positions and associate with the saved TileSet
            for (Tile tile : tiles) {
                PlayerMoveTileDto tileDto = playerMoveTileSetDto.tiles().stream()
                        .filter(dto -> dto.id().equals(tile.getId()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Tile ID mismatch: " + tile.getId()));

                tile.setGridColumn(tileDto.gridColumn());
                tile.setGridRow(tileDto.gridRow());
                tile.setDeck(null);
                tile.setTileSet(tileSet);
            }

            tileSet.setTiles(new HashSet<>(tiles));
            tileSetRepository.save(tileSet);
            existingTileSets.remove(tileSet);
        }
        tileSetRepository.flush();
        if (!existingTileSets.isEmpty()) {
            Set<UUID> incomingTileSetIds = playerMoveTileSetDtos.stream()
                    .map(PlayerMoveTileSetDto::id)
                    .filter(Objects::nonNull) // Excluding new TileSets that don't have an ID
                    .collect(Collectors.toSet());


            // Finding TileSets that are missing from the incoming DTOs
            List<TileSet> tileSetsToRemove = existingTileSets.stream()
                    .filter(tileSet -> !incomingTileSetIds.contains(tileSet.getId()))
                    .collect(Collectors.toList());

            // Removing the missing TileSets
            tileSetRepository.deleteAll(tileSetsToRemove);
        }
    }

    private TileSet createNewTileSet(UUID gameId) {
        TileSet tileSet = new TileSet();
        Game game = gameRepository.findByGameId(gameId)
                .orElseThrow(() -> new IllegalStateException("No game found"));
        tileSet.setPlayingField(game.getPlayingField());
        return tileSet;
    }

    @Transactional
    public void handlePlayerDeck(UUID playerId, List<PlayerMoveTileDto> deckDto) {
        moveValidationService.validateTileAttributes(deckDto);

        Player player = playerRepository.findPlayerById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found for ID: " + playerId));
        Deck deck = playerRepository.findPlayerById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found for ID: " + playerId))
                .getDeck();


        // Extract tile IDs from the DTO
        List<UUID> tileIds = deckDto.stream()
                .map(PlayerMoveTileDto::id)
                .collect(Collectors.toList());

        // Retrieve the tiles from the database
        List<Tile> tiles = tileRepository.findAllById(tileIds);
        if (tiles.size() != tileIds.size()) {
            throw new IllegalArgumentException("Some tiles from the deck DTO do not exist in the database.");
        }

        // Update the deck with the new tiles
        deck.getTiles().clear();
        deck.getTiles().addAll(tiles);

        List<Tile> saveTiles = new ArrayList<>();

        // Update tile positions based on the DTO
        for (Tile tile : tiles) {
            PlayerMoveTileDto tileDto = deckDto
                    .stream()
                    .filter(dto -> dto.id().equals(tile.getId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Tile ID mismatch in deck: " + tile.getId()));

            tile.setGridColumn(tileDto.gridColumn());
            tile.setGridRow(tileDto.gridRow());
            tile.setDeck(deck);
            saveTiles.add(tile);
        }

        // Save the updated tiles and deck
        tileRepository.saveAll(saveTiles);
        deckRepository.save(deck);

        // Update the player's score
        player.updateScore();
        playerRepository.save(player);
        log.info("Score of player: {}, updated to {}", playerId, player.getScore());
    }
}
