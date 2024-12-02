package kdg.be.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
public class TilePosition {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    private Tile tile;

    @ManyToOne(fetch = FetchType.LAZY)
    private Game game;

    private int rowPosition;
    private int columnPosition;

    public TilePosition() {
    }

    public TilePosition(Tile tile, Game game, int rowPosition, int columnPosition) {
        this.tile = tile;
        this.game = game;
        this.rowPosition = rowPosition;
        this.columnPosition = columnPosition;
    }
}
