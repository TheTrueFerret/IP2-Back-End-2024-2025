package kdg.be.backend.repository;

import kdg.be.backend.domain.GameUserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GameUserAchievementRepository extends JpaRepository<GameUserAchievement, UUID> {
    List<GameUserAchievement> findByGameUser_Id(UUID gameUserId);
    List<GameUserAchievement> findByAchievement_Id(long achievementId);
    List<GameUserAchievement> findByGameUser_IdAndAchievement_Id(UUID userId, long achievementId);
}
