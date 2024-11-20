package kdg.be.backend.domain;

import jakarta.persistence.*;
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
    //        private Color color;
    private int numberValue;

    // relaties
    @ManyToOne(fetch = FetchType.LAZY)
    private TileSet tileSet;

    public Tile() {
    }

    public Tile(int numberValue, TileSet tileSet) {
        this.numberValue = numberValue;
        this.tileSet = tileSet;
    }
}
