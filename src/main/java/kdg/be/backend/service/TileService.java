package kdg.be.backend.service;

import kdg.be.backend.domain.Tile;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

@Service
public class TileService {


    public void checkTileSet(LinkedList<Tile> tiles) {
        if (tiles.size() < 3) {
            throw new IllegalStateException("The tile set must contain at least 3 tiles.");
        }

        for (int i = 0; i < tiles.size(); i++) {
            // Skip the first tile
            if (i > 0) {
                Tile currentTile = tiles.get(i);
                Tile previousTile = tiles.get(i - 1);

                //If last tile is a joker, skip check
                if (i == tiles.size() - 1 && currentTile.getNumberValue() == 0 || previousTile.getNumberValue() == 0) {
                    continue;
                }

                if (currentTile.getNumberValue() == 0) {
                    // Check if joker is last tile
                    if (i < tiles.size() - 1) {
                        Tile nextTile = tiles.get(i + 1);

                        // Jokers bridging a sequence for the same color tiles
                        if (nextTile.getTileColor().equals(previousTile.getTileColor())) {
                            if (nextTile.getNumberValue() != previousTile.getNumberValue() + 2) {
                                throw new IllegalStateException("Joker does not correctly bridge a sequence for tiles of the same color.");
                            }
                        }
                        // Jokers matching numbers between different colors tiles
                        else {
                            if (nextTile.getNumberValue() != previousTile.getNumberValue()) {
                                throw new IllegalStateException("Joker does not correctly match numbers between different colors.");
                            }
                        }
                    }
                    continue;
                }


                if (currentTile.getTileColor().equals(previousTile.getTileColor())) {
                    if (currentTile.getNumberValue() != previousTile.getNumberValue() + 1) {
                        throw new IllegalStateException("Tiles of the same color must be in sequential order.");
                    }
                } else {
                    if (currentTile.getNumberValue() != previousTile.getNumberValue()) {
                        throw new IllegalStateException("Tiles of different colors must have the same number.");
                    }
                }
            }
        }
    }

}
