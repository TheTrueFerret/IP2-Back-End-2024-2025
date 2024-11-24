package kdg.be.backend.service;

import kdg.be.backend.domain.Player;
import kdg.be.backend.domain.Tile;
import kdg.be.backend.repository.PlayerRepository;
import kdg.be.backend.repository.TileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class GameService {
    private final TileRepository tileRepository;
    private final PlayerRepository playerRepository;

    public GameService(TileRepository tileRepository, PlayerRepository playerRepository) {
        this.tileRepository = tileRepository;
        this.playerRepository = playerRepository;
    }

    public List<Tile> getTilesOfPlayer(UUID playerId) {
        return tileRepository.findTilesByPlayerId(playerId);
    }

    public List<Player> getPlayersOfGame(UUID gameId) {
        return playerRepository.findPlayersByGameId(gameId);
    }
    //test
}
