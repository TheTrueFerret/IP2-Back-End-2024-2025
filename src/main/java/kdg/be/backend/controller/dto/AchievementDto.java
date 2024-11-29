package kdg.be.backend.controller.dto;

public record AchievementDto(
        String title,
        String description,
        boolean completed) {
}
