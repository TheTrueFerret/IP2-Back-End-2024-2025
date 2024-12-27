package kdg.be.backend.service;

import kdg.be.backend.domain.TilePool;
import kdg.be.backend.repository.GameRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class TilePoolService {
    private final GameRepository gameRepository;

    public TilePoolService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public Optional<TilePool> getTilePoolByGameId(UUID gameId) {
        return gameRepository.findTilePoolByGameId(gameId);
    }
}
