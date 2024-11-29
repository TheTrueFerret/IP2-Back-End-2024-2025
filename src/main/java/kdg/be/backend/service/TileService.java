package kdg.be.backend.service;

import kdg.be.backend.domain.Tile;
import kdg.be.backend.service.dto.CheckResult;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

@Service
public class TileService {


    public CheckResult checkTileSet(LinkedList<Tile> tiles) {
        if (tiles.size() < 3) {
            return new CheckResult(false, "The tile set must contain at least 3 tiles.");
        }

        for (int i = 0; i < tiles.size(); i++) {
            // Skip the firs tile
            if (i > 0) {
                Tile currentTile = tiles.get(i);
                Tile previousTile = tiles.get(i - 1);

                // Check for color
                if (currentTile.getTileColor().equals(previousTile.getTileColor())) {
                    //check order
                    if (currentTile.getNumberValue() != previousTile.getNumberValue() + 1) {
                        return new CheckResult(false, "Tiles are not in the correct sequential order for their color.");
                    }
                }
                // Check number but different color
                else if (currentTile.getNumberValue() != previousTile.getNumberValue()) {
                    return new CheckResult(false, "Tiles of different colors must have the same number.");
                }
            }
        }
        return new CheckResult(true, "The tile set is valid.");
    }
}
