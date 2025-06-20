package kdg.be.backend.controller.api;

import jakarta.validation.Valid;
import kdg.be.backend.controller.dto.game.GameDto;
import kdg.be.backend.controller.dto.mapper.GameDtoMapper;
import kdg.be.backend.controller.dto.requests.CreateGameSettingsRequest;
import kdg.be.backend.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Iterator;
import java.util.UUID;

@RestController
@RequestMapping("/api/games")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/start/{lobbyId}")
    public ResponseEntity<GameDto> startGame(@PathVariable UUID lobbyId, @Valid @RequestBody CreateGameSettingsRequest req) {
        return gameService.startGame(lobbyId, req.turnTime(), req.startTileAmount(), req.hostUserId())
                .map(GameDtoMapper::mapToGameDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/lobby/{lobbyId}")
    public ResponseEntity<UUID> getGameIdByLobbyIdAndUserId(@PathVariable UUID lobbyId, @RequestParam UUID userId) {
        return gameService.getGameIdByLobbyIdAndUserId(lobbyId, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/player/{playerId}")
    public  ResponseEntity<UUID> getGameIdByPlayerId(@PathVariable UUID playerId) {
        return gameService.getGameIdByPlayerId(playerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/{gameId}/leaderboard")
    public ResponseEntity<List<UUID>> getGameLeaderboard(@PathVariable UUID gameId) {
        return ResponseEntity.ok(gameService.getGameLeaderboard(gameId));
    }

    // TODO add TEST
    @GetMapping("/leave/{playerId}")
    public ResponseEntity<Boolean> leaveGame(@PathVariable UUID playerId) {
        return gameService.leaveGame(playerId)
                ? ResponseEntity.ok(true)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}