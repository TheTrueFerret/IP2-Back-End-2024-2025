package kdg.be.backend.controller.dto.game;

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
    private List<AchievementDto> achievements;
    private List<GameUserDto> friendList;
    private int gamesPlayed;
    private int gamesWon;

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
        this.achievements = new ArrayList<>();
        this.friendList = new ArrayList<>();
        gameUser.getFriendList().forEach(friend -> this.friendList.add(new GameUserDto(friend)));
        gameUser.getAchievements().forEach(achievement -> this.achievements.add(new AchievementDto(achievement.getAchievement().getTitle(), achievement.getAchievement().getDescription(), achievement.getAchievement().isCompleted())));
    }
    public GameUserDto(GameUser gameUser,int gamesPlayed,int gamesWon) {
        this.id = gameUser.getId();
        this.username = gameUser.getUsername();
        this.avatar = gameUser.getAvatar();
        this.achievements = new ArrayList<>();
        this.friendList = new ArrayList<>();
        gameUser.getFriendList().forEach(friend -> this.friendList.add(new GameUserDto(friend)));
        gameUser.getAchievements().forEach(achievement -> this.achievements.add(new AchievementDto(achievement.getAchievement().getTitle(), achievement.getAchievement().getDescription(), achievement.getAchievement().isCompleted())));
        this.gamesPlayed = gamesPlayed;
        this.gamesWon = gamesWon;
    }
}