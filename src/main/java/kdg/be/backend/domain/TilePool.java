package kdg.be.backend.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Entity
@Getter
@Setter
public class TilePool {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // relaties
    @OneToMany(mappedBy = "tilePool", cascade = CascadeType.ALL)
    private List<Tile> tiles;

    public TilePool() {
        this.tiles = new ArrayList<>();
    }

    public TilePool(List<Tile> tiles) {
        this.tiles = tiles;
    }

    public int getRemainingTilesSize() {
        return tiles.size();
    }

    // Shuffle the tiles
    public void shuffleTiles() {
        Collections.shuffle(tiles);
    }

    // Draw a tile from the pool
    public Tile drawTile() {
        if (tiles.isEmpty()) {
            throw new IllegalStateException("No tiles left in the pool!");
        }
        // Remove and return the first tile
        return tiles.removeFirst();
    }

    // Check if the pool is empty
    public boolean isEmpty() {
        return tiles.isEmpty();
    }
}
