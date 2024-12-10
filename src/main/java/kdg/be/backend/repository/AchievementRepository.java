package kdg.be.backend.repository;

import kdg.be.backend.domain.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {

    @Query("SELECT g.achievement FROM GameUserAchievement g " +
            "WHERE g.gameUser.id = :userId")
    List<Achievement> findAchievementsByUserId(UUID userId);
}
