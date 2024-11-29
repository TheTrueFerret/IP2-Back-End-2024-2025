package kdg.be.backend.controller.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AchievementDto {

    private String title;
    private String description;
    private boolean completed;

    public AchievementDto() {
    }

    public AchievementDto(String title, String description, boolean completed) {
        this.title = title;
        this.description = description;
        this.completed = completed;
    }
}
