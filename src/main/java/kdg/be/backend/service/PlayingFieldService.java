package kdg.be.backend.service;

import kdg.be.backend.controller.dto.requests.PlayerMoveTileDto;
import kdg.be.backend.controller.dto.requests.PlayerMoveTileSetDto;
import kdg.be.backend.domain.PlayingField;
import kdg.be.backend.domain.Tile;
import kdg.be.backend.domain.TileSet;
import kdg.be.backend.repository.PlayingFieldRepository;
import kdg.be.backend.repository.TileRepository;
import kdg.be.backend.repository.TileSetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlayingFieldService {
    private final PlayingFieldRepository playingFieldRepository;
    private final TileSetRepository tileSetRepository;
    private final TileRepository tileRepository;

    public PlayingFieldService(PlayingFieldRepository playingFieldRepository, TileSetRepository tileSetRepository, TileRepository tileRepository) {
        this.playingFieldRepository = playingFieldRepository;
        this.tileSetRepository = tileSetRepository;
        this.tileRepository = tileRepository;
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
            }

            // Save the updated tiles
            tileRepository.saveAll(tiles);

            TileSet tileSet;

            if (playerMoveTileSetDto.tileSetId() == null) {
                // Create a new TileSet if no ID is provided
                tileSet = new TileSet();
            } else {
                // Retrieve the existing TileSet if ID is provided
                tileSet = tileSetRepository.findById(playerMoveTileSetDto.tileSetId())
                        .orElseThrow(() -> new IllegalArgumentException("TileSet not found for ID: " + playerMoveTileSetDto.tileSetId()));
            }

            // Assign the updated tiles to the TileSet
            tileSet.getTiles().clear(); // Clear existing tiles if any
            tileSet.getTiles().addAll(tiles);

            // Save the TileSet and add it to the result list
            updatedTileSets.add(tileSetRepository.save(tileSet));
        }
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
