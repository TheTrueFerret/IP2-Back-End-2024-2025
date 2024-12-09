package kdg.be.backend.service;

import kdg.be.backend.domain.user.Achievement;
import kdg.be.backend.domain.user.GameUser;
import kdg.be.backend.domain.user.GameUserAchievement;
import kdg.be.backend.repository.AchievementRepository;
import kdg.be.backend.repository.GameUserAchievementRepository;
import kdg.be.backend.repository.GameUserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class GameUserAchievementService {

    private final GameUserAchievementRepository gameUserAchievementRepository;
    private final GameUserRepository gameUserRepository;
    private final AchievementRepository achievementRepository;

    public GameUserAchievementService(GameUserAchievementRepository gameUserAchievementRepository,
                                      GameUserRepository gameUserRepository,
                                      AchievementRepository achievementRepository) {
        this.gameUserAchievementRepository = gameUserAchievementRepository;
        this.gameUserRepository = gameUserRepository;
        this.achievementRepository = achievementRepository;
    }

    public List<GameUserAchievement> getAchievementsForUser(UUID userId) {
        return gameUserAchievementRepository.findByGameUser_Id(userId);
    }

    public List<GameUserAchievement> getUsersForAchievement(long achievementId) {
        return gameUserAchievementRepository.findByAchievement_Id(achievementId);
    }

    public GameUserAchievement addAchievementToUser(UUID userId, long achievementId) {
        GameUser user = gameUserRepository.findById(userId).orElseThrow(() ->
                new IllegalArgumentException("User not found with ID: " + userId));
        Achievement achievement = achievementRepository.findById(achievementId).orElseThrow(() ->
                new IllegalArgumentException("Achievement not found with ID: " + achievementId));

        GameUserAchievement userAchievement = new GameUserAchievement(user, achievement, LocalDateTime.now());
        return gameUserAchievementRepository.save(userAchievement);
    }

    public void removeAchievementFromUser(UUID userId, long achievementId) {
        List<GameUserAchievement> userAchievements = gameUserAchievementRepository.findByGameUser_Id(userId);
        GameUserAchievement toRemove = userAchievements.stream()
                .filter(ua -> ua.getAchievement().getId() == achievementId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Achievement not found for user"));

        gameUserAchievementRepository.delete(toRemove);
    }
}
