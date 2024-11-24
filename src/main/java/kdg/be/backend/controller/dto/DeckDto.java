package kdg.be.backend.controller.dto;

import kdg.be.backend.controller.dto.tiles.TileDto;

import java.util.List;

public record DeckDto(List<TileDto> deckTilesDto) { }