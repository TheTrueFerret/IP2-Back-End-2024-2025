package kdg.be.backend.controller.dto.tiles;

import java.util.List;
import java.util.UUID;

public record TileSetDto(UUID id, int startCoord, int endCoord, int gridRow, List<TileDto> tiles) { }