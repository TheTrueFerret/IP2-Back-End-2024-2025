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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tile> tiles;

    public TilePool() {
        this.tiles = new ArrayList<>();
    }

    public TilePool(List<Tile> tiles) {
        this.tiles = tiles;
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
        // Remove and return the first tile (or any specific logic for "drawing")
        return tiles.removeFirst();
    }

    // Add tiles to the pool (if replenishment is needed)
    public void addTiles(List<Tile> newTiles) {
        tiles.addAll(newTiles);
    }

    // Check if the pool is empty
    public boolean isEmpty() {
        return tiles.isEmpty();
    }
}
