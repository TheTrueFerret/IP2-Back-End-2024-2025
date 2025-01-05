package kdg.be.backend.controller.dto.game;

import java.time.LocalDateTime;

public record GameUserAchievementDto(
        String title,
        String description,
        LocalDateTime dateAchieved) {
}