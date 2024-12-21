package kdg.be.backend.controller.dto.game;

import kdg.be.backend.domain.enums.LobbyStatus;

import java.util.List;
import java.util.UUID;

public record LobbyDto(UUID id, LobbyStatus status, GameUserDto hostUser, List<GameUserDto> users, String joinCode,
                       int minimumPlayers, int maximumPlayers) {
}