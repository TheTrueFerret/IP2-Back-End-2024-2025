package kdg.be.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
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
    private int startTileAmount;
    private LocalDateTime dateTime;

    // relaties
    @ManyToOne(fetch = FetchType.LAZY)
    private PlayingField playingField;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private TilePool tilePool;

    @OneToMany
    private List<Player> players;

    public Game() {
    }  // jpa

    public Game(int roundTime, int startTileAmount, LocalDateTime dateTime, PlayingField playingField, TilePool tilePool, List<Player> players) {
        this.roundTime = roundTime;
        this.startTileAmount = startTileAmount;
        this.dateTime = dateTime;
        this.playingField = playingField;
        this.tilePool = tilePool;
        this.players = players;
    }
}
