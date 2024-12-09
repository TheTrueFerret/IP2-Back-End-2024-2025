package kdg.be.backend.domain.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String title;
    private String description;
    private boolean completed;

    @OneToMany(mappedBy = "achievement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameUserAchievement> users = new ArrayList<>();

    public Achievement() {
    }  // jpa

    public Achievement(String title, String description, boolean completed) {
        this.title = title;
        this.description = description;
        this.completed = completed;
    }
}
