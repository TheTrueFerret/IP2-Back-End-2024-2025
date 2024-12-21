package kdg.be.backend.controller.api;

import kdg.be.backend.controller.dto.tiles.TilePoolDto;
import kdg.be.backend.service.TilePoolService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static kdg.be.backend.controller.dto.mapper.GameDtoMapper.mapToTilePoolDto;

@RestController
@RequestMapping("/api/tilePools")
public class TilePoolController {
    private final TilePoolService tilePoolService;

    public TilePoolController(TilePoolService tilePoolService) {
        this.tilePoolService = tilePoolService;
    }

    @GetMapping("/contents")
    public ResponseEntity<TilePoolDto> getTilePoolTiles(@RequestParam UUID gameId) {
        return tilePoolService.getTilePoolByGameId(gameId)
                .map(tilePool -> ResponseEntity.ok(mapToTilePoolDto(tilePool)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
