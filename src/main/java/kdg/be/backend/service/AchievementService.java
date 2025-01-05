package kdg.be.backend.service;

import kdg.be.backend.domain.user.Achievement;
import kdg.be.backend.repository.AchievementRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AchievementService {
    private final AchievementRepository achievementRepository;

    public AchievementService(AchievementRepository achievementRepository) {
        this.achievementRepository = achievementRepository;
    }

    public List<Achievement> getAchievements() {
        return achievementRepository.findAll();
    }
}
