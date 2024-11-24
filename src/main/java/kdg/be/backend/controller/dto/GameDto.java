package kdg.be.backend.controller.dto;

import kdg.be.backend.controller.dto.tiles.TilePoolDto;

import java.util.List;

public record GameDto(int roundTime, int startTileAmount, PlayingFieldDto playingField, TilePoolDto tilePool,
                      List<PlayerDto> players) { }