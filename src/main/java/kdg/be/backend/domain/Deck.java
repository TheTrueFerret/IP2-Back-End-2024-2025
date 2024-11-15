package kdg.be.backend.domain;

//import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

//@Entity
@Getter
@Setter
public class Deck {
    //@Id
//    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private int deckNumber;

    // relaties
//    @OneToMany
    private List<Tile> tiles;
//    @ManyToOne(fetch = FetchType.LAZY)
    private Player player;

    public Deck() {
    } // jpa

    public Deck(int deckNumber, List<Tile> tiles, Player player) {
        this.deckNumber = deckNumber;
        this.tiles = tiles;
        this.player = player;
    }
}
