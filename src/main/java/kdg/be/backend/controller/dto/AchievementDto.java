package kdg.be.backend.controller.dto;


import lombok.Getter;
import lombok.Setter;


public record AchievementDto(
        String title,
        String description,
        boolean completed) {
}
