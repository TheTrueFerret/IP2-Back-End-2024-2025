package kdg.be.backend.domain.ai;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Prediction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private int id;
    @ManyToOne
    private GameStat gameStat;
    private double rating_average;
    private double complexity_average;
    private int owned_users;

    public Prediction() {
        // jpa
    }
}
