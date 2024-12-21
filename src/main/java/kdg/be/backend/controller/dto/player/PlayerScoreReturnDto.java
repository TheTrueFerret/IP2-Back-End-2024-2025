package kdg.be.backend.controller.dto;

import java.util.UUID;

public record PlayerScoreReturnDto (UUID playerId, int score) {
}
