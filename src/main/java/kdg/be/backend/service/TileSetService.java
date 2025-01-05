package kdg.be.backend.service;


import kdg.be.backend.controller.dto.requests.CreateTilesetRequest;
import kdg.be.backend.domain.PlayingField;
import kdg.be.backend.domain.Tile;
import kdg.be.backend.domain.TileSet;
import kdg.be.backend.repository.PlayingFieldRepository;
import kdg.be.backend.repository.TileRepository;
import kdg.be.backend.repository.TileSetRepository;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.stream.Collectors;


@Service
public class TileSetService {
    private final TileSetRepository tileSetRepository;
    private final TileRepository tileRepository;
    private final PlayingFieldRepository playingFieldRepository;


    public TileSetService(TileSetRepository tileSetRepository, TileRepository tileRepository, PlayingFieldRepository playingFieldRepository) {
        this.tileSetRepository = tileSetRepository;
        this.tileRepository = tileRepository;
        this.playingFieldRepository = playingFieldRepository;
    }


    public void createTileset(int startCoordinate, int endCoordinate, List<UUID> tileIds, UUID playingFieldId) {
        // Create the TileSet
        TileSet newTileSet = new TileSet();
        newTileSet.setStartCoordinate(startCoordinate);
        newTileSet.setEndCoordinate(endCoordinate);

        // Fetch the PlayingField
        PlayingField playingField = playingFieldRepository.findByIdWithTileSets(playingFieldId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find PlayingField to connect TileSet."));
        newTileSet.setPlayingField(playingField);

        // Fetch and associate Tiles with the TileSet
        List<Tile> tiles = tileRepository.findAllByIdWithTileSetAndTiles(tileIds);
        if (tiles.size() != tileIds.size()) {
            throw new RuntimeException("Some tiles not found.");
        }
        // Save the TileSet to generate its ID
        tileSetRepository.save(newTileSet); // This saves the TileSet and generates its ID

        // Initialize the collection of tiles for the new TileSet
        for (Tile tile : tiles) {

            // Detach the tile from its current TileSet if it belongs to one
            TileSet currentTileSet = tile.getTileSet();
            if (currentTileSet != null) {
                currentTileSet.getTiles().remove(tile);  // Remove the tile from the old TileSet
                tileSetRepository.save(currentTileSet);  // Persist the changes to the old TileSet
            }

            // Associate the tile with the new TileSet
            tile.setTileSet(newTileSet);  // Set the new TileSet
        }


        // Log the new TileSet and its associated Tiles

        // Add the TileSet to the PlayingField
        playingField.getTileSets().add(newTileSet);

        // Save the updated PlayingField and TileSet again to persist changes
        playingFieldRepository.save(playingField);  // Save the PlayingField with the new TileSet
        tileSetRepository.save(newTileSet);
    }


    public TileSet getTileSetById(UUID tileSetId) {
        return tileSetRepository.findByIdWithTiles(tileSetId)
                .orElseThrow(() -> new IllegalArgumentException("TileSet not found"));
    }


    public void addTileToTileSet(TileSet tileSet, Tile tile) {
        tileSet.getTiles().add(tile);
        tileSetRepository.save(tileSet);
    }
}