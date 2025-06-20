package kdg.be.backend.domain;

import jakarta.persistence.*;
import kdg.be.backend.domain.enums.GameState;
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
    private GameState gameState;
    private int turnTime;
    private int startTileAmount;
    private LocalDateTime dateTime;

    @ElementCollection
    private List<UUID> playerTurnOrder;
    @ElementCollection
    private List<UUID> playerTurnHistory;
    @ElementCollection
    private List<UUID> playerLeaderboard;

    // relaties
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "playing_field_id", referencedColumnName = "id")
    private PlayingField playingField;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private TilePool tilePool;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Player> players;

    @ManyToOne
    private Lobby lobby;

    public Game() {
    }  // jpa

    public Game(int turnTime, int startTileAmount, LocalDateTime dateTime, PlayingField playingField, TilePool tilePool, List<Player> players, Lobby lobby) {
        this.turnTime = turnTime;
        this.startTileAmount = startTileAmount;
        this.dateTime = dateTime;
        this.playingField = playingField;
        this.tilePool = tilePool;
        this.players = players;
        this.playerTurnOrder = new ArrayList<>();
        this.lobby = lobby;
        this.playerTurnHistory = new ArrayList<>();
        this.gameState = GameState.ONGOING;
        this.playerLeaderboard = new ArrayList<>();
    }
}