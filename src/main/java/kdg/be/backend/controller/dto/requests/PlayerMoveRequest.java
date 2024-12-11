package kdg.be.backend.controller.dto.requests;

import kdg.be.backend.controller.dto.tiles.TileSetDto;

import java.util.List;
import java.util.UUID;

public record PlayerMoveRequest(UUID gameId,
                                UUID playerId,
                                List<PlayerMoveTileSetDto> tileSets) {
}
