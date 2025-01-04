package kdg.be.backend.controller.dto.requests;

import java.util.List;
import java.util.UUID;

public record PlayerMoveTileSetDto(UUID id, int startCoord, int endCoord, int gridRow, List<PlayerMoveTileDto> tiles) {}
