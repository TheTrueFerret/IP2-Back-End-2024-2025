package kdg.be.backend.controller.api;

import jakarta.validation.Valid;
import kdg.be.backend.controller.dto.player.PlayerDto;
import kdg.be.backend.controller.dto.mapper.GameDtoMapper;
import kdg.be.backend.controller.dto.requests.CreateSimpleRequest;
import kdg.be.backend.controller.dto.requests.PlayerMoveRequest;
import kdg.be.backend.controller.dto.tiles.TileDto;
import kdg.be.backend.service.TurnService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static kdg.be.backend.controller.dto.mapper.GameDtoMapper.mapToPlayerDto;

@RestController
@RequestMapping("/api/turns")
public class TurnController {
    private final TurnService turnService;

    public TurnController(TurnService turnService) {
        this.turnService = turnService;
    }

    @PostMapping("/player-make-move")
    public ResponseEntity<PlayerDto> makePlayerMove(@Valid @RequestBody PlayerMoveRequest req) {
        return turnService.managePlayerMoves(req.playerId(), req.gameId(), req.playingField(), req.deck())
                .map(player -> ResponseEntity.ok(mapToPlayerDto(player)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PatchMapping("/player-pull-tile")
    public ResponseEntity<TileDto> getPulledTileFromTilePool(@Valid @RequestBody CreateSimpleRequest req) {
        return turnService.managePullingTileFromTilePool(req.gameId(), req.playerId())
                .map(tile -> ResponseEntity.ok(GameDtoMapper.mapToTileDto(tile)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
