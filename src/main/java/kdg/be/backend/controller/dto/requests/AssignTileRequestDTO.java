package kdg.be.backend.controller.dto.requests;

import kdg.be.backend.domain.enums.TileColor;

import java.util.UUID;

public record AssignTileRequestDTO(UUID gameId, UUID tileId, int row, int column) {}
