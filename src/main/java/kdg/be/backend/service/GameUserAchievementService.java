package kdg.be.backend.service;

import kdg.be.backend.domain.Achievement;
import kdg.be.backend.domain.GameUser;
import kdg.be.backend.domain.GameUserAchievement;
import kdg.be.backend.repository.AchievementRepository;
import kdg.be.backend.repository.GameUserAchievementRepository;
import kdg.be.backend.repository.GameUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class GameUserAchievementService {

    private final GameUserAchievementRepository gameUserAchievementRepository;
    private final GameUserRepository gameUserRepository;
    private final AchievementRepository achievementRepository;

    @Value("${achievement.participation.games-required}")
    private int gamesRequiredForParticipation;

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

    public void addAchievementToUser(UUID userId, long achievementId) {
        GameUser user = gameUserRepository.findById(userId).orElseThrow(() ->
                new IllegalArgumentException("User not found with ID: " + userId));
        Achievement achievement = achievementRepository.findById(achievementId).orElseThrow(() ->
                new IllegalArgumentException("Achievement not found with ID: " + achievementId));

        GameUserAchievement userAchievement = new GameUserAchievement(user, achievement, LocalDateTime.now());
        gameUserAchievementRepository.save(userAchievement);
    }

    public void removeAchievementFromUser(UUID userId, long achievementId) {
        List<GameUserAchievement> userAchievements = gameUserAchievementRepository.findByGameUser_Id(userId);
        GameUserAchievement toRemove = userAchievements.stream()
                .filter(ua -> ua.getAchievement().getId() == achievementId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Achievement not found for user"));

        gameUserAchievementRepository.delete(toRemove);
    }

    public void checkAndAssignFirstMoveAchievement(UUID userId) {
        long achievementId = 1;
        gameUserRepository.findById(userId).orElseThrow(() ->
                new IllegalArgumentException("User not found with ID: " + userId));
        Achievement achievement = achievementRepository.findById(achievementId).orElseThrow(() ->
                new IllegalArgumentException("Achievement not found with ID: " + achievementId));

        if (gameUserAchievementRepository.findByGameUser_IdAndAchievement_Id(userId, achievement.getId()).isEmpty()) {
            addAchievementToUser(userId, achievement.getId());
        }
    }

    public void checkAndAssignParticipationAchievement(UUID userId) {
        gameUserRepository.findById(userId).orElseThrow(() ->
                new IllegalArgumentException("User not found with ID: " + userId));
        long gamesPlayed = gameUserRepository.countGamesPlayedByUser(userId);

        if (gamesPlayed >= gamesRequiredForParticipation && gameUserAchievementRepository.findByGameUser_IdAndAchievement_Id(userId, 2).isEmpty()) {
            addAchievementToUser(userId, 2);
        }
    }
}
