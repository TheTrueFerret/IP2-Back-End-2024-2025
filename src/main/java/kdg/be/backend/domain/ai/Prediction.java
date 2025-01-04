package kdg.be.backend.domain.ai;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Prediction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "game_stat_id", nullable = false)
    private GameStat gameStat;

    private LocalDateTime prediction_date;
    private double rating_average;
    private double complexity_average;
    private int owned_users;

    public Prediction() {
        // jpa
    }
}
