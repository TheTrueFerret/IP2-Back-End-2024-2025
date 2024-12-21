package kdg.be.backend.controller.dto.player;

import java.util.UUID;

public record PlayerScoreReturnDto (UUID playerId, int score) {
}
