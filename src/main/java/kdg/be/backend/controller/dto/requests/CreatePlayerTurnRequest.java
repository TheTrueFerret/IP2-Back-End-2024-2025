package kdg.be.backend.controller.dto.requests;

import java.util.UUID;

public record CreatePlayerTurnRequest(
        UUID gameId,
        UUID playerId
) {
}