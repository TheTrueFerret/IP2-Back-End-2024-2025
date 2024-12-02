package kdg.be.backend.service;

import kdg.be.backend.controller.dto.requests.CreateTilesetRequest;
import kdg.be.backend.domain.PlayingField;
import kdg.be.backend.domain.Tile;
import kdg.be.backend.domain.TileSet;
import kdg.be.backend.repository.PlayingFieldRepository;
import kdg.be.backend.repository.TileRepository;
import kdg.be.backend.repository.TileSetRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
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

    public TileSet createTileset(CreateTilesetRequest request) {
        // Create the Tileset
        TileSet newTileSet = new TileSet();
        newTileSet.setStartCoordinate(request.startCoordinate());
        newTileSet.setEndCoordinate(request.endCoordinate());

        // Fetch and associate Tiles with the Tileset
        List<Tile> tiles = tileRepository.findAllById(request.tileIds());
        if (tiles.size() != request.tileIds().size()) {
            throw new RuntimeException("Some tiles not found.");
        }
        Set<Tile> tilesToSet = tiles.stream().collect(Collectors.toSet());
        newTileSet.setTiles(tilesToSet);

        // Save the new TileSet
        return tileSetRepository.save(newTileSet);
    }

    public PlayingField assignTilesetToPlayingField(UUID tilesetId, UUID playingFieldId) {
        TileSet tileSet = tileSetRepository.findByIdWithTiles(tilesetId)
                .orElseThrow(() -> new RuntimeException("Tileset not found: " + tilesetId));
        PlayingField playingField = playingFieldRepository.findByIdWithTileSets(playingFieldId)
                .orElseThrow(() -> new RuntimeException("Playing Field not found: " + playingFieldId));

        playingField.getTileSets().add(tileSet);
        playingFieldRepository.save(playingField);

        return playingField;
    }

    public List<TileSet> getTilesetsByPlayingField(UUID playingFieldId) {
        List<TileSet> tileSets = tileSetRepository.findAllByPlayingFieldIdWithTiles(playingFieldId);
        if (tileSets.isEmpty()) {
            throw new RuntimeException("No TileSets found for Playing Field: " + playingFieldId);
        }
        return tileSets;
    }

    public TileSet getTileSetById(UUID tileSetId) {
        return tileSetRepository.findById(tileSetId)
                .orElseThrow(() -> new IllegalArgumentException("TileSet not found"));
    }

    public void addTileToTileSet(TileSet tileSet, Tile tile) {
        tileSet.getTiles().add(tile);
        tileSetRepository.save(tileSet);
    }


}
