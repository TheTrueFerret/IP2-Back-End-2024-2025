package kdg.be.backend.controller.dto.game;

import java.time.LocalDateTime;

public record AchievementDto(
        String title,
        String description,
        LocalDateTime dateAchieved) {
}
