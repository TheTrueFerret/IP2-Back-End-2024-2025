package kdg.be.backend.controller.dto.tiles;

import kdg.be.backend.domain.enums.TileColor;

import java.util.UUID;

public record TileDto(UUID id, int numberValue, TileColor color, int gridColumn, int gridRow) { }
