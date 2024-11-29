package kdg.be.backend.service;

import kdg.be.backend.domain.Achievement;
import kdg.be.backend.repository.AchievementRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AchievementService {

    private  AchievementRepository achievementRepository;

    public AchievementService(AchievementRepository achievementRepository) {
        this.achievementRepository = achievementRepository;
    }


    public List<Achievement> getAchievements(UUID userId) {
        return achievementRepository.findAchievementsByUserId(userId);
    }
}
