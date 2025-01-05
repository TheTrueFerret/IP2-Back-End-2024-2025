package kdg.be.backend.domain.customization;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import kdg.be.backend.domain.user.GameUser;
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

    @ManyToOne
    @JoinColumn(name = "game_user_id")
    private GameUser gameUser;

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
