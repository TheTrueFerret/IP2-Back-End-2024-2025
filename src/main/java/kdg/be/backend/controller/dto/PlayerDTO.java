package kdg.be.backend.controller.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
@Getter
public class PlayerDTO {
    private final UUID id;
    private final String username;
    private final UUID gameId;

    public PlayerDTO(UUID id, String username, UUID gameId) {
        this.id = id;
        this.username = username;
        this.gameId = gameId;
    }

}
