package kdg.be.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@Setter
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private int turnTime;
    private int startTileAmount;
    private LocalDateTime dateTime;
    @ElementCollection
    private List<UUID> playerTurnOrder;

    // relaties
    @ManyToOne(fetch = FetchType.LAZY)
    private PlayingField playingField;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private TilePool tilePool;

    @OneToMany(mappedBy = "game",  cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Player> players;

    public Game() {
    }  // jpa

    public Game(int turnTime, int startTileAmount, LocalDateTime dateTime, PlayingField playingField, TilePool tilePool, List<Player> players) {
        this.turnTime = turnTime;
        this.startTileAmount = startTileAmount;
        this.dateTime = dateTime;
        this.playingField = playingField;
        this.tilePool = tilePool;
        this.players = players;
        this.playerTurnOrder = new ArrayList<>();
    }
}