package kdg.be.backend.controller.api;

import kdg.be.backend.controller.dto.tiles.TileDto;
import kdg.be.backend.domain.Tile;
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
@RequestMapping("/api/tiles")
public class TileController {
    private final TileService tileService;

    public TileController(TileService tileService) {
        this.tileService = tileService;
    }


    @GetMapping("/by-tilepool/{tilePoolId}")
    public List<TileDto> getTilesByTilePoolId(@PathVariable UUID tilePoolId) {
        return tileService.getTilesByTilePoolId(tilePoolId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/by-tileset/{tileSetId}")
    public List<TileDto> getTilesByTileSetId(@PathVariable UUID tileSetId) {
        return tileService.getTilesByTileSetId(tileSetId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/by-deck/{deckId}")
    public List<TileDto> getTilesByDeckId(@PathVariable UUID deckId) {
        return tileService.getTilesByDeckId(deckId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", IllegalArgumentException.class.getSimpleName());
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    private TileDto mapToDto(Tile tile) {
        return new TileDto(
                tile.getNumberValue(),
                tile.getTileColor(),
                tile.getGridColumn(),
                tile.getGridRow()
        );
    }
}
