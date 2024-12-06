package kdg.be.backend.controller.api;

import kdg.be.backend.controller.dto.PlayingFieldDto;
import kdg.be.backend.controller.dto.requests.CreateTilesetRequest;
import kdg.be.backend.controller.dto.tiles.TileDto;
import kdg.be.backend.controller.dto.tiles.TileSetDto;
import kdg.be.backend.domain.PlayingField;
import kdg.be.backend.domain.TileSet;
import kdg.be.backend.service.TileSetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tilesets")
public class TilesetController {
    private final TileSetService tilesetService;

    public TilesetController(TileSetService tilesetService) {
        this.tilesetService = tilesetService;
    }

    @PostMapping
    public ResponseEntity<TileSetDto> createTileset(@RequestBody CreateTilesetRequest request) {
        TileSet tileSet = tilesetService.createTileset(request);
        return ResponseEntity.ok(toDTO(tileSet));
    }

    @GetMapping("/playingfield/{playingFieldId}")
    public ResponseEntity<List<TileSetDto>> getTilesetsByPlayingField(@PathVariable UUID playingFieldId) {
        List<TileSet> tileSets = tilesetService.getTilesetsByPlayingField(playingFieldId);
        List<TileSetDto> tileSetDtos = tileSets.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(tileSetDtos);
    }

    private TileSetDto toDTO(TileSet tileSet) {
        List<TileDto> tileDtos = tileSet.getTiles().stream()
                .map(tile -> new TileDto(tile.getNumberValue(), tile.getTileColor(), tile.getGridColumn(), tile.getGridRow()))
                .collect(Collectors.toList());

        return new TileSetDto(
                tileSet.getStartCoordinate(),
                tileSet.getEndCoordinate(),
                tileDtos
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", IllegalArgumentException.class.getSimpleName());
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}