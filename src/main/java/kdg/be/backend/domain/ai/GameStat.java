package kdg.be.backend.domain.ai;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
public class GameStat {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID gameId;
    private String gameName;
    private int year_published;
    private int min_players;
    private int max_players;
    private int play_time;
    private int min_age;
    private int board_game_honor;
    private String mechanics;

    @OneToMany
    private List<Prediction> predictions;

    public GameStat() {
        // jpa
    }
}
