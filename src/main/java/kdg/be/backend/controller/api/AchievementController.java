package kdg.be.backend.controller.api;

import kdg.be.backend.controller.dto.game.AchievementDto;
import kdg.be.backend.domain.user.Achievement;
import kdg.be.backend.service.AchievementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/achievements")
public class AchievementController {
    private final AchievementService achievementService;

    public AchievementController(AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    @GetMapping
    public ResponseEntity<List<AchievementDto>> getAchievements() {
        List<Achievement> achievements = achievementService.getAchievements();

        List<AchievementDto> achievementDtos = achievements.stream().map(achievement -> {
            return new AchievementDto(
                    achievement.getTitle(),
                    achievement.getDescription()
            );
        }).collect(Collectors.toList());
        return ResponseEntity.ok(achievementDtos);
    }
}
