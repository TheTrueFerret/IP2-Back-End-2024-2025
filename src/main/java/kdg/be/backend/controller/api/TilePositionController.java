package kdg.be.backend.controller.api;

import kdg.be.backend.controller.dto.requests.AssignTileRequestDTO;
import kdg.be.backend.controller.dto.tiles.TilePositionDTO;
import kdg.be.backend.domain.Game;
import kdg.be.backend.domain.Tile;
import kdg.be.backend.domain.TilePosition;
import kdg.be.backend.service.GameService;
import kdg.be.backend.service.TilePositionService;
import kdg.be.backend.service.TileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tile-positions")
public class TilePositionController {
    private final TilePositionService tilePositionService;
    private final GameService gameService;
    private final TileService tileService;

    public TilePositionController(TilePositionService tilePositionService, GameService gameService, TileService tileService) {
        this.tilePositionService = tilePositionService;
        this.gameService = gameService;
        this.tileService = tileService;
    }

    @GetMapping("/game/{gameId}")
    public ResponseEntity<List<TilePositionDTO>> getTilePositionsForGame(@PathVariable UUID gameId) {
        Game game = gameService.getGameById(gameId);
        List<TilePositionDTO> tilePositions = tilePositionService
                .getTilePositionsForGame(game)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(tilePositions);
    }

    @PostMapping("/assign")
    public ResponseEntity<TilePositionDTO> assignTileToPosition(@RequestBody AssignTileRequestDTO request) {
        Game game = gameService.getGameById(request.gameId());
        var tile = tileService.getTileById(request.tileId());

        TilePosition tilePosition = tilePositionService.assignTileToPosition(
                game,
                tile,
                request.row(),
                request.column()
        );

        return ResponseEntity.ok(toDTO(tilePosition));
    }

    private TilePositionDTO toDTO(TilePosition tilePosition) {
        Tile tile = tilePosition.getTile();
        return new TilePositionDTO(
                tilePosition.getId(),
                tilePosition.getRowPosition(),
                tilePosition.getColumnPosition(),
                tile.getId(),
                tile.getTileColor(),
                tile.getNumberValue()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "IllegalArgumentException");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
