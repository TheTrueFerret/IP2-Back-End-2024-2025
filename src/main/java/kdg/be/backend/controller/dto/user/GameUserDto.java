package kdg.be.backend.controller.dto.user;

import kdg.be.backend.controller.dto.AchievementDto;
import kdg.be.backend.domain.user.GameUser;
import kdg.be.backend.domain.user.GameUserAchievement;
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
    }

    public GameUserDto(GameUser gameUser, int gamesPlayed, int gamesWon) {
        this.id = gameUser.getId();
        this.username = gameUser.getUsername();
        this.avatar = gameUser.getAvatar();
        this.achievements = new ArrayList<>();
        this.friendList = new ArrayList<>();
        if (!gameUser.getFriendList().isEmpty()) {
            gameUser.getFriendList().forEach(friend -> this.friendList.add(new GameUserDto(friend)));
        }
        if (!gameUser.getAchievements().isEmpty()) {
            gameUser.getAchievements().forEach(achievement -> this.achievements.add(new AchievementDto(achievement.getAchievement().getTitle(), achievement.getAchievement().getDescription(), achievement.getAchievement().isCompleted())));
        }
        this.gamesPlayed = gamesPlayed;
        this.gamesWon = gamesWon;
    }

    public GameUserDto(GameUser gameUser, List<GameUserAchievement> achievements) {
        this.id = gameUser.getId();
        this.username = gameUser.getUsername();
        this.avatar = gameUser.getAvatar();
        this.achievements = new ArrayList<>();
        achievements.forEach(achievement -> this.achievements.add(new AchievementDto(achievement.getAchievement().getTitle(), achievement.getAchievement().getDescription(), achievement.getAchievement().isCompleted())));
    }
}