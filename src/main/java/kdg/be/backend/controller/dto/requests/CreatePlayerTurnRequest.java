package kdg.be.backend.controller.dto.requests;

import kdg.be.backend.controller.dto.MoveType;

import java.util.List;
import java.util.UUID;

public record CreatePlayerTurnRequest(
        UUID gameId,
        UUID playerId,
        MoveType moveType,
        int startCoordinate,
        int endCoordinate,
        UUID tileSet,
        List<UUID> tileIds,
        UUID playingFieldId
) {
}