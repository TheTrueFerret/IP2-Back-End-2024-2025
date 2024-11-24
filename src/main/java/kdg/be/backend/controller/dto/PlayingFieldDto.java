package kdg.be.backend.controller.dto;

import kdg.be.backend.controller.dto.tiles.TileSetDto;

import java.util.List;

public record PlayingFieldDto(List<TileSetDto> tileSetDtos) { }