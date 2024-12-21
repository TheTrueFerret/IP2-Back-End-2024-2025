package kdg.be.backend.controller.dto;

import java.util.UUID;

public record AddTileToTilesetRequestDTO(UUID playingFieldId, UUID tileSetId, UUID tileId) {
}
