package kdg.be.backend.domain.customization;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

import java.util.UUID;

@Getter
@Entity
public class Customizable {
    @Id
    private UUID id;
    private String name;
    private String description;
    private String color;
    private int points;

    public Customizable() {
        //JPA
    }

    public Customizable(String name, String description, int points, String color) {
        this.name = name;
        this.description = description;
        this.points = points;
        this.color = color;
    }
}
