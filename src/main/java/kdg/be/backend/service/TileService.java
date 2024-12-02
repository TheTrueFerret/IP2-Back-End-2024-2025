package kdg.be.backend.service;

import kdg.be.backend.domain.Tile;
import kdg.be.backend.repository.TileRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TileService {
    private final TileRepository tileRepository;

    public TileService(TileRepository tileRepository) {
        this.tileRepository = tileRepository;
    }

    public List<Tile> getAllTiles() {
        return tileRepository.findAll();
    }

    public Tile getTileById(UUID id) {
        return tileRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Tile not found"));
    }
}
