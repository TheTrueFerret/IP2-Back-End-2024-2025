package kdg.be.backend.controller.api;

import kdg.be.backend.controller.dto.AddTileToTilesetRequestDTO;
import kdg.be.backend.controller.dto.tiles.TileDto;
import kdg.be.backend.controller.dto.tiles.TileSetDto;
import kdg.be.backend.domain.TileSet;
import kdg.be.backend.service.PlayingFieldService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/playing-fields")
public class PlayingFieldController {
    private final PlayingFieldService playingFieldService;

    public PlayingFieldController(PlayingFieldService playingFieldService) {
        this.playingFieldService = playingFieldService;
    }

    @PostMapping("/add-tile")
    public ResponseEntity<TileSetDto> addTileToTileSet(@RequestBody AddTileToTilesetRequestDTO request) {
        // Call the service to add the tile to the TileSet
        TileSet updatedTileSet = playingFieldService.addTileToTileSet(
                request.playingFieldId(),
                request.tileSetId(),
                request.tileId()
        );

        // Map the TileSet entity to TileSetDto before returning it
        TileSetDto tileSetDto = toDto(updatedTileSet);

        // Return the updated TileSetDto
        return ResponseEntity.ok(tileSetDto);
    }

    private TileSetDto toDto(TileSet tileSet) {
        // Map the TileSet entity to TileSetDto
        List<TileDto> tileDtos = tileSet.getTiles().stream()
                .map(tile -> new TileDto(tile.getNumberValue(), tile.getTileColor(), tile.getGridColumn(), tile.getGridRow()))
                .collect(Collectors.toList());

        return new TileSetDto(tileSet.getStartCoordinate(), tileSet.getEndCoordinate(), tileDtos);
    }
}
