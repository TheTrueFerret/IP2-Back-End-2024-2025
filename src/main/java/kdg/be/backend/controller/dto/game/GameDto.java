package kdg.be.backend.controller.dto.game;

import kdg.be.backend.controller.dto.player.PlayerDto;
import kdg.be.backend.controller.dto.tiles.TilePoolDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record GameDto(UUID gameId, int turnTime, int startTileAmount, LocalDateTime dateTime, List<UUID> playerTurnOrder, PlayingFieldDto playingField, TilePoolDto tilePool,
                      List<PlayerDto> players, LobbyDto lobby) { }