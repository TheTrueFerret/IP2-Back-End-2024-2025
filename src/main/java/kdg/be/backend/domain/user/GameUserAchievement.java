package kdg.be.backend.domain.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class GameUserAchievement {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "gameuser_id", nullable = false)
    private GameUser gameUser;

    @ManyToOne
    @JoinColumn(name = "achievement_id", nullable = false)
    private Achievement achievement;

    private LocalDateTime dateAchieved;

    public GameUserAchievement() {
    }

    public GameUserAchievement(GameUser gameUser, Achievement achievement, LocalDateTime dateAchieved) {
        this.gameUser = gameUser;
        this.achievement = achievement;
        this.dateAchieved = dateAchieved;
    }
}
