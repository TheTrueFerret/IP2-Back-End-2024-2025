package kdg.be.backend.controller.dto;

import kdg.be.backend.domain.GameUser;

import java.util.UUID;

public class GameUserDTO {
    private UUID id;
    private String username;

    public GameUserDTO() {
    }

    public GameUserDTO(String username, UUID id) {
        this.id = id;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public UUID getId() {
        return id;
    }
}
