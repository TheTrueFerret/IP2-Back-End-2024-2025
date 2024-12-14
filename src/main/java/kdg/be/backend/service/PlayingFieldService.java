package kdg.be.backend.service;

import kdg.be.backend.controller.dto.requests.PlayerMoveDeckDto;
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

    private static final Logger log = LoggerFactory.getLogger(GameService.class);

    public PlayingFieldService(PlayingFieldRepository playingFieldRepository, TileSetRepository tileSetRepository, TileRepository tileRepository, DeckRepository deckRepository, PlayerRepository playerRepository) {
        this.playingFieldRepository = playingFieldRepository;
        this.tileSetRepository = tileSetRepository;
        this.tileRepository = tileRepository;
        this.deckRepository = deckRepository;
        this.playerRepository = playerRepository;
    }

    @Transactional
    public void handlePlayerMoves(List<PlayerMoveTileSetDto> playerMoveTileSetDtos) {
        List<TileSet> updatedTileSets = new ArrayList<>();

        for (PlayerMoveTileSetDto playerMoveTileSetDto : playerMoveTileSetDtos) {
            // Extract tile IDs from the DTO
            List<UUID> tileIds = playerMoveTileSetDto.tiles().stream()
                    .map(PlayerMoveTileDto::tileId)
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
            } else {
                // Retrieve the existing TileSet if ID is provided
                tileSet = tileSetRepository.findByIdWithTiles(playerMoveTileSetDto.tileSetId())
                        .orElseThrow(() -> new IllegalArgumentException("TileSet not found for ID: " + playerMoveTileSetDto.tileSetId()));
            }

            // Assign the updated tiles to the TileSet
            tileSet.getTiles().clear(); // Clear existing tiles if any
            tileSet.getTiles().addAll(tiles);
            tileSet.setStartCoordinate(playerMoveTileSetDto.startCoordinate());
            tileSet.setEndCoordinate(playerMoveTileSetDto.endCoordinate());

            // Update tile positions based on the DTO
            for (Tile tile : tiles) {
                PlayerMoveTileDto tileDto = playerMoveTileSetDto.tiles()
                        .stream()
                        .filter(dto -> dto.tileId().equals(tile.getId()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Tile ID mismatch: " + tile.getId()));

                // Update the tile's column and row
                tile.setGridColumn(tileDto.gridColumn());
                tile.setGridRow(tileDto.gridRow());
                tile.setTileSet(tileSet);
            }

            // Save the updated tiles
            tileRepository.saveAll(tiles);

            // Save the TileSet and add it to the result list
            updatedTileSets.add(tileSetRepository.save(tileSet));
        }
    }

    @Transactional
    public void handlePlayerDeck(UUID playerId, PlayerMoveDeckDto playerDeckDto) {
        // Retrieve the player's deck via the PlayerRepository
        Player player = playerRepository.findPlayerById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found for ID: " + playerId));
        Deck deck = playerRepository.findPlayerById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found for ID: " + playerId))
                .getDeck();


        // Extract tile IDs from the DTO
        List<UUID> tileIds = playerDeckDto.tilesInDeck().stream()
                .map(PlayerMoveTileDto::tileId)
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
            PlayerMoveTileDto tileDto = playerDeckDto.tilesInDeck()
                    .stream()
                    .filter(dto -> dto.tileId().equals(tile.getId()))
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
