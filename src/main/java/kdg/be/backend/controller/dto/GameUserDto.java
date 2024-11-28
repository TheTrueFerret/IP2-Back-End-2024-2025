package kdg.be.backend.controller.dto;

import kdg.be.backend.domain.Achievement;
import kdg.be.backend.domain.GameUser;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class GameUserDto {
    private UUID id;
    private String username;
    private String avatar;
    private List<Achievement> achievements;
    private List<GameUser> friendList;

    public GameUserDto() {
    }

    public GameUserDto(String username, UUID id) {
        this.id = id;
        this.username = username;
    }

    public GameUserDto(String username, String avatar) {
        this.avatar = avatar;
        this.username = username;
    }

    public GameUserDto(GameUser gameUser) {
        this.id = gameUser.getId();
        this.username = gameUser.getUsername();
        this.avatar = gameUser.getAvatar();
        this.achievements = gameUser.getAchievements() != null ? gameUser.getAchievements() : new ArrayList<>();
        this.friendList = gameUser.getFriendList() != null ? gameUser.getFriendList() : new ArrayList<>();

    }
}