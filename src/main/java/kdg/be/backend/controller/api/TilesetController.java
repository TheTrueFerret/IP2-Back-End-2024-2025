package kdg.be.backend.controller.api;

import kdg.be.backend.controller.dto.PlayingFieldDto;
import kdg.be.backend.controller.dto.requests.CreateTilesetRequest;
import kdg.be.backend.controller.dto.tiles.TileDto;
import kdg.be.backend.controller.dto.tiles.TileSetDto;
import kdg.be.backend.domain.PlayingField;
import kdg.be.backend.domain.TileSet;
import kdg.be.backend.service.TileSetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @PutMapping("/{tilesetId}/assign-to-playingfield/{playingFieldId}")
    public ResponseEntity<PlayingFieldDto> assignTileSetToPlayingField(
            @PathVariable UUID tilesetId,
            @PathVariable UUID playingFieldId) {
        PlayingField playingField = tilesetService.assignTilesetToPlayingField(tilesetId, playingFieldId);
        return ResponseEntity.ok(toPlayingFieldDto(playingField));
    }

    @GetMapping("/playingfield/{playingFieldId}")
    public ResponseEntity<List<TileSetDto>> getTilesetsByPlayingField(@PathVariable UUID playingFieldId) {
        List<TileSet> tileSets = tilesetService.getTilesetsByPlayingField(playingFieldId);
        List<TileSetDto> tileSetDtos = tileSets.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(tileSetDtos);
    }

    private PlayingFieldDto toPlayingFieldDto(PlayingField playingField) {
        List<TileSetDto> tileSetDtos = playingField.getTileSets().stream()
                .map(this::toDTO)
                .toList();
        return new PlayingFieldDto(tileSetDtos);
    }

    private TileSetDto toDTO(TileSet tileSet) {
        List<TileDto> tileDtos = tileSet.getTiles().stream()
                .map(tile -> new TileDto(tile.getNumberValue(), tile.getTileColor()))
                .collect(Collectors.toList());

        return new TileSetDto(
                tileSet.getStartCoordinate(),
                tileSet.getEndCoordinate(),
                tileDtos
        );
    }
}