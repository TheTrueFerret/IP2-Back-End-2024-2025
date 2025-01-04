package kdg.be.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Entity
@Getter
@Setter
public class TileSet {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private int startCoordinate;
    private int endCoordinate;
    private int gridRow;

    @OneToMany(mappedBy = "tileSet", cascade = CascadeType.ALL)
    private Set<Tile> tiles;

    @ManyToOne
    private PlayingField playingField;

    public TileSet() {
    }  // jpa

    public TileSet(int startCoordinate, int endCoordinate, int gridRow, Set<Tile> tiles, PlayingField playingField) {
        this.startCoordinate = startCoordinate;
        this.endCoordinate = endCoordinate;
        this.gridRow = gridRow;
        this.tiles = tiles;
        this.playingField = playingField;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TileSet tileSet = (TileSet) o;
        return Objects.equals(id, tileSet.id);
    }
}
