package kdg.be.backend.service;

import kdg.be.backend.TestContainerIPConfiguration;
import kdg.be.backend.controller.dto.requests.PlayerMoveTileDto;
import kdg.be.backend.controller.dto.requests.PlayerMoveTileSetDto;
import kdg.be.backend.controller.dto.tiles.TileDto;
import kdg.be.backend.domain.Tile;
import kdg.be.backend.domain.TileSet;
import kdg.be.backend.domain.enums.TileColor;
import kdg.be.backend.exception.TileSetException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestContainerIPConfiguration.class)
class TileServiceTest {

    @Autowired
    private MoveValidationService moveValidationService;

    @Test
    void TileServiceTestOnColorDontThrow() {
        List<PlayerMoveTileDto> tiles = new LinkedList<>(List.of(
                new PlayerMoveTileDto(UUID.randomUUID(), 1, TileColor.RED, 1, 1),
                new PlayerMoveTileDto(UUID.randomUUID(), 2, TileColor.RED, 2, 1),
                new PlayerMoveTileDto(UUID.randomUUID(), 3, TileColor.RED, 3, 1),
                new PlayerMoveTileDto(UUID.randomUUID(), 4, TileColor.RED, 4, 1),
                new PlayerMoveTileDto(UUID.randomUUID(), 5, TileColor.RED, 5, 1)
        ));
        assertDoesNotThrow(() -> moveValidationService.checkTileSet(tiles));
    }

    @Test
    void TileServiceTestOnNumbersDontThrow() {
        List<PlayerMoveTileDto> tiles = new LinkedList<>(List.of(
                new PlayerMoveTileDto(UUID.randomUUID(), 11, TileColor.RED, 1, 1),
                new PlayerMoveTileDto(UUID.randomUUID(), 11, TileColor.ORANGE, 2, 1),
                new PlayerMoveTileDto(UUID.randomUUID(), 11, TileColor.BLUE, 3, 1)
        ));
        assertDoesNotThrow(() -> moveValidationService.checkTileSet(tiles));
    }

    @Test
    void TileServiceTestThrowException() {
        List<PlayerMoveTileDto> tiles = new LinkedList<>(List.of(
                new PlayerMoveTileDto(UUID.randomUUID(), 11, TileColor.RED, 1, 1),
                new PlayerMoveTileDto(UUID.randomUUID(), 11, TileColor.ORANGE, 2, 1)
        ));
        assertThrows(TileSetException.class, () -> moveValidationService.checkTileSet(tiles));
    }

    @Test
    void TileServiceTestThrowExceptionSetWrongColors() {
        List<PlayerMoveTileDto> tiles = new LinkedList<>(List.of(
                new PlayerMoveTileDto(UUID.randomUUID(), 1, TileColor.RED, 1, 1),
                new PlayerMoveTileDto(UUID.randomUUID(), 2, TileColor.BLUE, 2, 1),
                new PlayerMoveTileDto(UUID.randomUUID(), 3, TileColor.RED, 3, 1),
                new PlayerMoveTileDto(UUID.randomUUID(), 4, TileColor.RED, 4, 1),
                new PlayerMoveTileDto(UUID.randomUUID(), 5, TileColor.BLACK, 5, 1)
        ));
        assertThrows(TileSetException.class, () -> moveValidationService.checkTileSet(tiles));
    }

    @Test
    void TileServiceTestThrowExceptionSetWrongNumbers() {
        List<PlayerMoveTileDto> tiles = new LinkedList<>(List.of(
                new PlayerMoveTileDto(UUID.randomUUID(), 1, TileColor.RED, 1, 1),
                new PlayerMoveTileDto(UUID.randomUUID(), 2, TileColor.RED, 2, 1),
                new PlayerMoveTileDto(UUID.randomUUID(), 3, TileColor.RED, 3, 1),
                new PlayerMoveTileDto(UUID.randomUUID(), 7, TileColor.RED, 4, 1),
                new PlayerMoveTileDto(UUID.randomUUID(), 8, TileColor.RED, 5, 1)
        ));
        assertThrows(TileSetException.class, () -> moveValidationService.checkTileSet(tiles));
    }

    @Test
    void TileServiceTestWithJokerInSequenceDontThrow() {
        List<PlayerMoveTileDto> tiles = new LinkedList<>(List.of(
                new PlayerMoveTileDto(UUID.randomUUID(), 1, TileColor.RED, 1, 1),
                new PlayerMoveTileDto(UUID.randomUUID(), 2, TileColor.RED, 2, 1),
                new PlayerMoveTileDto(UUID.randomUUID(), 0, TileColor.RED, 3, 1), // <----------Joker
                new PlayerMoveTileDto(UUID.randomUUID(), 4, TileColor.RED, 4, 1),
                new PlayerMoveTileDto(UUID.randomUUID(), 5, TileColor.RED, 5, 1)
        ));
        assertDoesNotThrow(() -> moveValidationService.checkTileSet(tiles));
    }

    @Test
    void TileServiceTestWithJokerDifferentColorsDontThrow() {
        List<PlayerMoveTileDto> tiles = new LinkedList<>(List.of(
                new PlayerMoveTileDto(UUID.randomUUID(), 11, TileColor.RED, 1, 1),
                new PlayerMoveTileDto(UUID.randomUUID(), 0, TileColor.BLUE, 2, 1), // <----------Joker
                new PlayerMoveTileDto(UUID.randomUUID(), 11, TileColor.ORANGE, 3, 1),
                new PlayerMoveTileDto(UUID.randomUUID(), 11, TileColor.BLACK, 4, 1)
        ));
        assertDoesNotThrow(() -> moveValidationService.checkTileSet(tiles));
    }

    @Test
    void TileServiceTestWithJokerMisplacedThrowException() {
        List<PlayerMoveTileDto> tiles = new LinkedList<>(List.of(
                new PlayerMoveTileDto( UUID.randomUUID(), 9, TileColor.RED, 1, 1),
                new PlayerMoveTileDto(UUID.randomUUID(), 0, TileColor.BLUE, 2, 1), // <----------Joker
                new PlayerMoveTileDto(UUID.randomUUID(), 12, TileColor.BLACK, 3, 1)
        ));
        assertThrows(TileSetException.class, () -> moveValidationService.checkTileSet(tiles));
    }

    @Test
    void TileServiceTestWithJokersThrowException() {
        List<PlayerMoveTileDto> tiles = new LinkedList<>(List.of(
                new PlayerMoveTileDto(UUID.randomUUID(), 9, TileColor.RED, 1, 1),
                new PlayerMoveTileDto(UUID.randomUUID(), 0, TileColor.BLUE, 2, 1), // <----------Joker
                new PlayerMoveTileDto(UUID.randomUUID(), 12, TileColor.BLACK, 3, 1)
        ));
        assertThrows(TileSetException.class, () -> moveValidationService.checkTileSet(tiles));
    }

}