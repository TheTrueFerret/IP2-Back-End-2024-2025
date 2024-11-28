package kdg.be.backend.controller.dto;

public class GameUserDTO {
    private String id;
    private String username;

    public GameUserDTO() {
    }

    public GameUserDTO(String username, String id) {
        this.id = id;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public String getId() {
        return id;
    }
}
