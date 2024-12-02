package kdg.be.backend.service;

import kdg.be.backend.domain.Tile;
import kdg.be.backend.domain.TileSet;
import kdg.be.backend.domain.enums.TileColor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TileServiceTest {

    @Autowired
    private TileService tileService;

    @Test
    void TileServiceTestOnColorDontThrow() {
        TileSet tileSet = new TileSet();
        LinkedList<Tile> tiles = new LinkedList<>(List.of(
                new Tile(1, TileColor.RED),
                new Tile(2, TileColor.RED),
                new Tile(3, TileColor.RED),
                new Tile(4, TileColor.RED),
                new Tile(5, TileColor.RED)));
        tileSet.setTiles(tiles);
        tileSet.setStartCoordinate(1);
        tileSet.setEndCoordinate(5);
        assertDoesNotThrow(() -> tileService.checkTileSet(tiles));
    }

    @Test
    void TileServiceTestOnNumbersDontThrow() {
        TileSet tileSet = new TileSet();
        LinkedList<Tile> tiles = new LinkedList<>(List.of(
                new Tile(11, TileColor.RED),
                new Tile(11, TileColor.ORANGE),
                new Tile(11, TileColor.BLUE)));
        tileSet.setTiles(tiles);
        tileSet.setStartCoordinate(1);
        tileSet.setEndCoordinate(3);
        assertDoesNotThrow(() -> tileService.checkTileSet(tiles));
    }

    @Test
    void TileServiceTestThrowException() {
        TileSet tileSet = new TileSet();
        LinkedList<Tile> tiles = new LinkedList<>(List.of(
                new Tile(11, TileColor.RED),
                new Tile(11, TileColor.ORANGE)));
        tileSet.setTiles(tiles);
        tileSet.setStartCoordinate(1);
        tileSet.setEndCoordinate(2);
        assertThrows(IllegalStateException.class, () -> tileService.checkTileSet(tiles));
    }

    @Test
    void TileServiceTestThrowExceptionSetWrongColors() {
        TileSet tileSet = new TileSet();
        LinkedList<Tile> tiles = new LinkedList<>(List.of(
                new Tile(1, TileColor.RED),
                new Tile(2, TileColor.BLUE),
                new Tile(3, TileColor.RED),
                new Tile(4, TileColor.RED),
                new Tile(5, TileColor.BLACK)));
        tileSet.setTiles(tiles);
        tileSet.setStartCoordinate(1);
        tileSet.setEndCoordinate(5);
        assertThrows(IllegalStateException.class, () -> tileService.checkTileSet(tiles));
    }

    @Test
    void TileServiceTestThrowExceptionSetWrongNumbers() {
        TileSet tileSet = new TileSet();
        LinkedList<Tile> tiles = new LinkedList<>(List.of(new Tile(1, TileColor.RED), new Tile(2, TileColor.RED), new Tile(3, TileColor.RED), new Tile(7, TileColor.RED), new Tile(8, TileColor.RED)));
        tileSet.setTiles(tiles);
        tileSet.setStartCoordinate(1);
        tileSet.setEndCoordinate(5);
        assertThrows(IllegalStateException.class, () -> tileService.checkTileSet(tiles));
    }

    //Joker checks
    @Test
    void TileServiceTestWithJokerInSequenceDontThrow() {
        TileSet tileSet = new TileSet();
        LinkedList<Tile> tiles = new LinkedList<>(List.of(
                new Tile(1, TileColor.RED),
                new Tile(2, TileColor.RED),
                new Tile(0, TileColor.RED), // <----------Joker
                new Tile(4, TileColor.RED),
                new Tile(5, TileColor.RED)
        ));
        tileSet.setTiles(tiles);
        tileSet.setStartCoordinate(1);
        tileSet.setEndCoordinate(5);
        assertDoesNotThrow(() -> tileService.checkTileSet(tiles));
    }

    @Test
    void TileServiceTestWithJokerDifferentColorsDontThrow() {
        TileSet tileSet = new TileSet();
        LinkedList<Tile> tiles = new LinkedList<>(List.of(
                new Tile(11, TileColor.RED),
                new Tile(0, TileColor.BLUE), // <----------Joker
                new Tile(11, TileColor.ORANGE),
                new Tile(11, TileColor.BLACK)
        ));
        tileSet.setTiles(tiles);
        tileSet.setStartCoordinate(1);
        tileSet.setEndCoordinate(4);
        assertDoesNotThrow(() -> tileService.checkTileSet(tiles));
    }

    @Test
    void TileServiceTestWithJokerMisplacedThrowException() {
        TileSet tileSet = new TileSet();
        LinkedList<Tile> tiles = new LinkedList<>(List.of(
                new Tile(1, TileColor.RED),
                new Tile(0, TileColor.RED), // <----------Joker
                new Tile(3, TileColor.RED),
                new Tile(5, TileColor.RED)
        ));
        tileSet.setTiles(tiles);
        tileSet.setStartCoordinate(1);
        tileSet.setEndCoordinate(4);
        assertThrows(IllegalStateException.class, () -> tileService.checkTileSet(tiles));
    }

    @Test
    void TileServiceTestWithJokersThrowException() {
        TileSet tileSet = new TileSet();
        LinkedList<Tile> tiles = new LinkedList<>(List.of(
                new Tile(9, TileColor.RED),
                new Tile(0, TileColor.BLUE), // <----------Joker
                new Tile(12, TileColor.BLACK)
        ));
        tileSet.setTiles(tiles);
        tileSet.setStartCoordinate(1);
        tileSet.setEndCoordinate(3);
        assertThrows(IllegalStateException.class, () -> tileService.checkTileSet(tiles));
    }

}