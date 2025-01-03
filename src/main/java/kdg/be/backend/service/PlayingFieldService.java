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

    public PlayingField getPlayingFieldByGameId(UUID gameId) {
        Game game = gameRepository.findGameByIdWithPlayingField(gameId)
                .orElseThrow(() -> new IllegalStateException("No game found"));

        return game.getPlayingField();
    }

    @Transactional
    public void handlePlayerMoves(UUID gameId, List<PlayerMoveTileSetDto> playerMoveTileSetDtos) {
        List<TileSet> updatedTileSets = new ArrayList<>();

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

            TileSet tileSet;

            if (playerMoveTileSetDto.tileSetId() == null) {
                // Create a new TileSet if no ID is provided
                tileSet = new TileSet();

                Game game = gameRepository.findByGameId(gameId)
                        .orElseThrow(() -> new IllegalStateException("No game found"));
                tileSet.setPlayingField(game.getPlayingField());

                // Update tile positions based on the DTO
                for (Tile tile : tiles) {
                    PlayerMoveTileDto tileDto = playerMoveTileSetDto.tiles()
                            .stream()
                            .filter(dto -> dto.id().equals(tile.getId()))
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("Tile ID mismatch: " + tile.getId()));

                    // Update the tile's column and row
                    tile.setGridColumn(tileDto.gridColumn());
                    tile.setGridRow(tileDto.gridRow());
                    tile.setTileSet(tileSet);
                }

                Set<Tile> tileSetSet = new HashSet<>(tiles);
                tileSet.setTiles(tileSetSet);
                tileSet.setGridRow(playerMoveTileSetDto.gridRow());
                tileSet.setStartCoordinate(playerMoveTileSetDto.startCoord());
                tileSet.setEndCoordinate(playerMoveTileSetDto.endCoord());

            } else {
                // Retrieve the existing TileSet if ID is provided
                tileSet = tileSetRepository.findByIdWithTiles(playerMoveTileSetDto.tileSetId())
                        .orElseThrow(() -> new IllegalArgumentException("TileSet not found for ID: " + playerMoveTileSetDto.tileSetId()));

                // Update tile positions based on the DTO
                for (Tile tile : tiles) {
                    PlayerMoveTileDto tileDto = playerMoveTileSetDto.tiles()
                            .stream()
                            .filter(dto -> dto.id().equals(tile.getId()))
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("Tile ID mismatch: " + tile.getId()));

                    // Update the tile's column and row
                    tile.setGridColumn(tileDto.gridColumn());
                    tile.setGridRow(tileDto.gridRow());
                    tile.setTileSet(tileSet);
                }

                // Assign the updated tiles to the TileSet
                tileSet.getTiles().clear(); // Clear existing tiles if any
                tileSet.getTiles().addAll(tiles);
                tileSet.setGridRow(playerMoveTileSetDto.gridRow());
                tileSet.setStartCoordinate(playerMoveTileSetDto.startCoord());
                tileSet.setEndCoordinate(playerMoveTileSetDto.endCoord());
            }

            // Save the updated tiles
            tileRepository.saveAll(tiles);

            // Save the TileSet and add it to the result list
            updatedTileSets.add(tileSetRepository.save(tileSet));
        }
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
        }

        // Save the updated tiles and deck
        tileRepository.saveAll(tiles);
        deckRepository.save(deck);

        // Update the player's score
        player.updateScore();
        playerRepository.save(player);
        log.info("Score of player: {}, updated to {}", playerId, player.getScore());
    }


    public TileSet addTileToTileSet(UUID playingFieldId, UUID tileSetId, UUID tileId) {
        // Get the playing field and tile set
        PlayingField playingField = playingFieldRepository.findByIdWithTileSets(playingFieldId)
                .orElseThrow(() -> new IllegalArgumentException("PlayingField not found"));
        TileSet tileSet = tileSetRepository.findByIdWithTiles(tileSetId)
                .orElseThrow(() -> new IllegalArgumentException("TileSet not found"));

        // Ensure the tileSet belongs to the playingField
        if (!playingField.getTileSets().contains(tileSet)) {
            throw new IllegalArgumentException("TileSet does not belong to this PlayingField");
        }

        // Retrieve the tile by its ID
        Tile tile = tileRepository.findById(tileId)
                .orElseThrow(() -> new IllegalArgumentException("Tile not found"));

        // Add the tile to the tile set
        tileSet.getTiles().add(tile);
        tile.setTileSet(tileSet);

        tileRepository.save(tile);
        tileSetRepository.save(tileSet);


        return tileSet; // Return the updated TileSet entity
    }
}
