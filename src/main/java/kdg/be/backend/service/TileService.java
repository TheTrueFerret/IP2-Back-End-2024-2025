package kdg.be.backend.service;

import kdg.be.backend.domain.Tile;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

@Service
public class TileService {


    public boolean checkTileSet(LinkedList<Tile> tiles) {
        boolean valid = true;
        for (int i = 0; i < tiles.size(); i++) {
            if (!tiles.getFirst().equals(tiles.get(i))) {
                if (tiles.get(i).getNumberValue() != tiles.get(i - 1).getNumberValue() - 1
                        && tiles.get(i).getTileColor().equals(tiles.get(i - 1).getTileColor()) ) {
                    return false;
                }
            }
        }

        return valid;
    }
}
