package kdg.be.backend.domain;

import jakarta.persistence.*;
import kdg.be.backend.domain.enums.TileColor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
public class Tile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Enumerated(EnumType.STRING)
    private TileColor tileColor;
    private int numberValue;
    private int gridColumn;
    private int gridRow;
    @ManyToOne
    private TilePool tilePool;
    @ManyToOne
    private TileSet tileSet;
    @ManyToOne
    private Deck deck;
    public Tile() {
    }

//    public Tile(int numberValue, TileColor tileColor) {
//        this.tileColor = tileColor;
//        this.numberValue = numberValue;
//    }

    //deze constructor gaat moeten gebruikt worden maar heb deze nog effe in commentaar gelaten
    public Tile(TileColor tileColor, int numberValue, TilePool tilePool, TileSet tileSet, Deck deck) {
        this.tileColor = tileColor;
        this.numberValue = numberValue;
        this.gridColumn = 0;
        this.gridRow = 0;
        this.tilePool = tilePool;
        this.tileSet = tileSet;
        this.deck = deck;
    }
}
