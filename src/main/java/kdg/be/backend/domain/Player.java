package kdg.be.backend.domain;

import jakarta.persistence.*;
import kdg.be.backend.domain.user.GameUser;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private LocalDateTime turnStartTime;
    private LocalDateTime turnEndTime;
    private LocalDateTime turnMoveTime;
    private int score;

    // relaties
    @ManyToOne(fetch = FetchType.LAZY)
    private GameUser gameUser;
    @ManyToOne(fetch = FetchType.LAZY)
    private Deck deck;
    @ManyToOne(fetch = FetchType.LAZY)
    private Game game;

    public Player() {
    }  // jpa

    public Player(GameUser gameUser, Deck deck, Game game) {
        this.gameUser = gameUser;
        this.deck = deck;
        this.game = game;
        updateScore();
    }

    public void updateScore() {
        if (deck != null && deck.getTiles() != null) {
            this.score = calculateScoreFromDeck();
        } else {
            this.score = 0;
        }
    }

    private int calculateScoreFromDeck() {
        return deck.getTiles().stream()
                .mapToInt(Tile::getNumberValue)
                .sum();
    }
}
