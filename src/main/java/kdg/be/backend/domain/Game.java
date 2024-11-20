package kdg.be.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private int roundTime;
    private int playerAmount;
    private int startTileAmount;
    private String joinCode;

     // relaties
    @ManyToOne(fetch = FetchType.LAZY)
    private PlayingField playingField;
    @OneToMany
    private List<Player> players;

    public Game() {
    }  // jpa

    public Game(int roundTime, int playerAmount, int startTileAmount, String joinCode, PlayingField playingField, List<Player> players) {
        this.roundTime = roundTime;
        this.playerAmount = playerAmount;
        this.startTileAmount = startTileAmount;
        this.joinCode = joinCode;
        this.playingField = playingField;
        this.players = players;
    }
}
