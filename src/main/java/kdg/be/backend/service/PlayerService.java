package kdg.be.backend.service;

import kdg.be.backend.domain.Player;
import kdg.be.backend.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
public class PlayerService {
    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public int getPlayerScore(UUID playerId) {
        return playerRepository.findById(playerId)
                .map(Player::getScore)
                .orElseThrow(() -> new IllegalArgumentException("Player not found with ID: " + playerId));
    }
}
