package kdg.be.backend.service;

import kdg.be.backend.domain.Tile;
import kdg.be.backend.domain.TileSet;
import kdg.be.backend.domain.enums.TileColor;
import kdg.be.backend.service.dto.CheckResult;
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
    void TileServiceTestOnColorReturnTrue() {
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
        CheckResult status = tileService.checkTileSet(tiles);
        assertTrue(status.isValid());
        assertEquals("The tile set is valid.", status.message());
    }

    @Test
    void TileServiceTestOnNumbersReturnTrue() {
        TileSet tileSet = new TileSet();
        LinkedList<Tile> tiles = new LinkedList<>(List.of(
                new Tile(11, TileColor.RED),
                new Tile(11, TileColor.ORANGE),
                new Tile(11, TileColor.BLUE)));
        tileSet.setTiles(tiles);
        tileSet.setStartCoordinate(1);
        tileSet.setEndCoordinate(3);
        CheckResult status = tileService.checkTileSet(tiles);
        assertTrue(status.isValid());
        assertEquals("The tile set is valid.", status.message());
    }

    @Test
    void TileServiceTestReturnFalseSetTooSmall() {
        TileSet tileSet = new TileSet();
        LinkedList<Tile> tiles = new LinkedList<>(List.of(
                new Tile(11, TileColor.RED),
                new Tile(11, TileColor.ORANGE)));
        tileSet.setTiles(tiles);
        tileSet.setStartCoordinate(1);
        tileSet.setEndCoordinate(2);
        CheckResult status = tileService.checkTileSet(tiles);
        assertFalse(status.isValid());
        assertEquals("The tile set must contain at least 3 tiles.", status.message());
    }

    @Test
    void TileServiceTestReturnFalseSetWrongColors() {
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
        CheckResult status = tileService.checkTileSet(tiles);
        assertFalse(status.isValid());
        assertEquals("Tiles of different colors must have the same number.", status.message());
    }

    @Test
    void TileServiceTestReturnFalseSetWrongNumbers() {
        TileSet tileSet = new TileSet();
        LinkedList<Tile> tiles = new LinkedList<>(List.of(new Tile(1, TileColor.RED), new Tile(2, TileColor.RED), new Tile(3, TileColor.RED), new Tile(7, TileColor.RED), new Tile(8, TileColor.RED)));
        tileSet.setTiles(tiles);
        tileSet.setStartCoordinate(1);
        tileSet.setEndCoordinate(5);
        CheckResult status = tileService.checkTileSet(tiles);
        assertFalse(status.isValid());
        assertEquals("Tiles of the same color must be in sequential order.", status.message());
    }

    //Joker checks
    @Test
    void TileServiceTestWithJokerInSequenceReturnTrue() {
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
        CheckResult status = tileService.checkTileSet(tiles);
        assertTrue(status.isValid());
        assertEquals("The tile set is valid.", status.message());
    }

    @Test
    void TileServiceTestWithJokerDifferentColorsReturnTrue() {
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
        CheckResult status = tileService.checkTileSet(tiles);
        assertTrue(status.isValid());
        assertEquals("The tile set is valid.", status.message());
    }

    @Test
    void TileServiceTestWithJokerMisplacedReturnFalse() {
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
        CheckResult status = tileService.checkTileSet(tiles);
        assertFalse(status.isValid());
        assertEquals("Tiles of the same color must be in sequential order.", status.message());
    }

    @Test
    void TileServiceTestWithJokersReturnFalse() {
        TileSet tileSet = new TileSet();
        LinkedList<Tile> tiles = new LinkedList<>(List.of(
                new Tile(9, TileColor.RED),
                new Tile(0, TileColor.BLUE), // <----------Joker
                new Tile(12, TileColor.BLACK)
        ));
        tileSet.setTiles(tiles);
        tileSet.setStartCoordinate(1);
        tileSet.setEndCoordinate(3);
        CheckResult status = tileService.checkTileSet(tiles);
        assertFalse(status.isValid());
        assertEquals("Joker does not correctly match numbers between different colors.", status.message());
    }

}