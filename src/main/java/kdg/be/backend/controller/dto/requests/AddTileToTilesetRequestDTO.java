package kdg.be.backend.controller.dto.requests;

import java.util.UUID;

public record AddTileToTilesetRequestDTO(UUID playingFieldId, UUID tileSetId, UUID tileId) {
}
