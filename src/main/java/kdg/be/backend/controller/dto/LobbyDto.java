package kdg.be.backend.controller.dto;

import kdg.be.backend.domain.enums.LobbyStatus;

import java.util.List;

public record LobbyDto(LobbyStatus status, GameUserDto hostUser, List<GameUserDto> users, String joinCode,
                       int minimumPlayers, int maximumPlayers) {
}