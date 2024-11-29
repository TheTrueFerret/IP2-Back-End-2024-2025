package kdg.be.backend.service;

import jakarta.validation.constraints.AssertTrue;
import kdg.be.backend.domain.Tile;
import kdg.be.backend.domain.TileSet;
import kdg.be.backend.domain.enums.TileColor;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TileServiceTest {

    @Mock
    private TileService tileService;

    @Test
    void TileServiceTestReturnTrue() {
        TileSet tileSet = new TileSet();
        LinkedList<Tile> tiles = (LinkedList<Tile>) List.of(new Tile(1, TileColor.RED), new Tile(2, TileColor.RED), new Tile(3, TileColor.RED), new Tile(4, TileColor.RED), new Tile(5, TileColor.RED));
        tileSet.setTiles(tiles);
        tileSet.setStartCoordinate(1);
        tileSet.setEndCoordinate(5);
        boolean status = tileService.checkTileSet(tiles);
        //TODO: Need assert
    }

    @Test
    void TileServiceTestReturnFalse() {

    }
}