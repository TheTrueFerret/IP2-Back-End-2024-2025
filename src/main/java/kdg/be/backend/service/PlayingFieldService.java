package kdg.be.backend.service;

import kdg.be.backend.domain.PlayingField;
import kdg.be.backend.domain.Tile;
import kdg.be.backend.domain.TileSet;
import kdg.be.backend.repository.PlayingFieldRepository;
import kdg.be.backend.repository.TileRepository;
import kdg.be.backend.repository.TileSetRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

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
        tileSetRepository.save(tileSet);

        return tileSet; // Return the updated TileSet entity
    }
}
