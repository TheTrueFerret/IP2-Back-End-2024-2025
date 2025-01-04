package kdg.be.backend.controller.dto.tiles;

import java.util.List;

public record TileSetDto(int startCoordinate, int endCoordinate, int gridRow, List<TileDto> tiles) { }