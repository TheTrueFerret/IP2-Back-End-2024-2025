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
    private TileColor tileColor;
    private int numberValue;

    public Tile() {
    }

    public Tile(int numberValue, TileColor tileColor) {
        this.numberValue = numberValue;
        this.tileColor = tileColor;
    }
}
