package kdg.be.backend.service;

import kdg.be.backend.domain.Tile;
import kdg.be.backend.repository.DeckRepository;
import kdg.be.backend.repository.TilePoolRepository;
import kdg.be.backend.repository.TileRepository;
import kdg.be.backend.repository.TileSetRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TileService {
    private final TileRepository tileRepository;
    private final TilePoolRepository tilePoolRepository;
    private final TileSetRepository tileSetRepository;
    private final DeckRepository deckRepository;

    public TileService(TileRepository tileRepository, TilePoolRepository tilePoolRepository, TileSetRepository tileSetRepository, DeckRepository deckRepository) {
        this.tileRepository = tileRepository;
        this.tilePoolRepository = tilePoolRepository;
        this.tileSetRepository = tileSetRepository;
        this.deckRepository = deckRepository;
    }

    public List<Tile> getAllTiles() {
        return tileRepository.findAll();
    }

    public Tile getTileById(UUID id) {
        return tileRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Tile not found"));
    }

    public List<Tile> getTilesByTilePoolId(UUID tilePoolId) {
        if (!tilePoolRepository.existsById(tilePoolId)) {
            throw new IllegalArgumentException("TilePool not found for ID: " + tilePoolId);
        }
        return tileRepository.findTilesByTilePoolId(tilePoolId);
    }

    public List<Tile> getTilesByTileSetId(UUID tileSetId) {
        if (!tileSetRepository.existsById(tileSetId)) {
            throw new IllegalArgumentException("TileSet not found for ID: " + tileSetId);
        }
        return tileRepository.findTilesByTileSetId(tileSetId);
    }

    public List<Tile> getTilesByDeckId(UUID deckId) {
        if (!deckRepository.existsById(deckId)) {
            throw new IllegalArgumentException("Deck not found for ID: " + deckId);
        }
        return tileRepository.findTilesByDeckId(deckId);
    }
}
