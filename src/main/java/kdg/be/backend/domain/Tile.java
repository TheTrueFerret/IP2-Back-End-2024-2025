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

    // relaties
    @ManyToOne
    private TilePool tilePool;
    @ManyToOne
    private TileSet tileSet;
    @ManyToOne
    private Deck deck;

    public Tile() {} // jpa

    public Tile(int numberValue, TileColor tileColor) {
        this.tileColor = tileColor;
        this.numberValue = numberValue;
        this.gridColumn = 0;
        this.gridRow = 0;
    }
}
