package kdg.be.backend.domain;

//import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

//@Entity
@Getter
@Setter
public class Player {
    //@Id
//    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // relaties
//    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
//    @ManyToOne(fetch = FetchType.LAZY)
    private Deck deck;
//    @ManyToOne(fetch = FetchType.LAZY)
    private Game game;

    public Player() {
    } // jpa

    public Player(User user, Deck deck, Game game) {
        this.user = user;
        this.deck = deck;
        this.game = game;
    }
}
