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
    private Queue<Tile> tiles; // Queue to represent the pool of tiles

    public TilePool() {
        this.tiles = new LinkedList<>();
    }

    public TilePool(Queue<Tile> tiles) {
        this.tiles = tiles;
    }

    // Shuffle the tiles
    public void shuffleTiles() {
        List<Tile> tileList = new ArrayList<>(tiles);
        Collections.shuffle(tileList);
        this.tiles = new LinkedList<>(tileList);
    }

    // Draw a tile from the pool
    public Tile drawTile() {
        if (tiles.isEmpty()) {
            throw new IllegalStateException("No tiles left in the pool!");
        }
        return tiles.poll();
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
