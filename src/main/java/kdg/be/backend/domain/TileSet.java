package kdg.be.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.UUID;

@Entity
@Getter
@Setter
public class TileSet {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private int startCoordinate;
    private int endCoordinate;

     // relaties
    @OneToMany
    private LinkedList<Tile> tiles;

    public TileSet() {
    }  // jpa

    public TileSet(int startCoordinate, int endCoordinate, LinkedList<Tile> tiles) {
        this.startCoordinate = startCoordinate;
        this.endCoordinate = endCoordinate;
        this.tiles = tiles;
    }
}
