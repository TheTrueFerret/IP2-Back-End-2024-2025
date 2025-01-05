package kdg.be.backend.controller.dto.requests;

import kdg.be.backend.domain.enums.TileColor;

import java.util.UUID;

public record PlayerMoveTileDto(UUID id, int numberValue, TileColor color, int gridColumn, int gridRow) {
}
