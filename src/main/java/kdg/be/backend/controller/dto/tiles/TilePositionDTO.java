package kdg.be.backend.controller.dto.tiles;

import kdg.be.backend.domain.enums.TileColor;

import java.util.UUID;

public record TilePositionDTO (UUID id, int rowPosition, int columnPosition, UUID tileId, TileColor tileColor,
                               int numberValue) {}
