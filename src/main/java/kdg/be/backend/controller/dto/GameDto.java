package kdg.be.backend.controller.dto;

import kdg.be.backend.controller.dto.tiles.TilePoolDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record GameDto(int turnTime, int startTileAmount, LocalDateTime dateTime, List<UUID> playerTurnOrder, PlayingFieldDto playingField, TilePoolDto tilePool,
                      List<PlayerDto> players) { }