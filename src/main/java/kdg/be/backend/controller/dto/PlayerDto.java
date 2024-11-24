package kdg.be.backend.controller.dto;

import lombok.Getter;

import java.util.UUID;
@Getter
public class PlayerDto {
    private final UUID id;
    private final String username;
    private final UUID gameId;

    public PlayerDto(UUID id, String username, UUID gameId) {
        this.id = id;
        this.username = username;
        this.gameId = gameId;
    }

}
