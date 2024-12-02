package kdg.be.backend.controller.dto.requests;

import java.util.List;
import java.util.UUID;

public record CreateTilesetRequest(int startCoordinate, int endCoordinate, List<UUID> tileIds, UUID playingFieldId) {
}
