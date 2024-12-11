package kdg.be.backend.controller.api;

import jakarta.validation.Valid;
import kdg.be.backend.controller.dto.GameDto;
import kdg.be.backend.controller.dto.PlayerDto;
import kdg.be.backend.controller.dto.mapper.GameDtoMapper;
import kdg.be.backend.controller.dto.requests.CreateGameSettingsRequest;
import kdg.be.backend.controller.dto.requests.CreatePlayerTurnRequest;
import kdg.be.backend.controller.dto.requests.CreateSimpleRequest;
import kdg.be.backend.controller.dto.requests.PlayerMoveRequest;
import kdg.be.backend.controller.dto.tiles.TileDto;
import kdg.be.backend.domain.Player;
import kdg.be.backend.domain.Tile;
import kdg.be.backend.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/game")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    private List<PlayerDto> mapToPlayerDTOs(List<Player> players) {
        return players.stream()
                .map(player -> new PlayerDto(
                        player.getId(),
                        player.getGameUser().getUsername(), // Assuming getGameUser() fetches the username
                        player.getGame().getId(),           // Assuming getGame() fetches the game ID
                        GameDtoMapper.mapToDeckDto(player.getDeck())
                ))
                .collect(Collectors.toList());
    }

    private PlayerDto mapToPlayerDTO(Player player) {
        return new PlayerDto(player.getId(), player.getGameUser().getUsername(), player.getGame().getId(), GameDtoMapper.mapToDeckDto(player.getDeck()));
    }

    @GetMapping("/tiles/player/{playerId}")
    public List<Tile> getTilesOfPlayer(@PathVariable UUID playerId) {
        return gameService.getTilesOfPlayer(playerId);
    }

    @GetMapping("/players/{gameId}")
    @Transactional
    public List<PlayerDto> getPlayersOfGame(@PathVariable UUID gameId) {
        List<Player> players = gameService.getPlayersOfGame(gameId);
        return mapToPlayerDTOs(players);
    }

    @PostMapping("/start/{lobbyId}")
    public ResponseEntity<GameDto> startGame(@PathVariable UUID lobbyId, @Valid @RequestBody CreateGameSettingsRequest req) {
        return gameService.startGame(lobbyId, req.turnTime(), req.startTileAmount(), req.hostUserId())
                .map(GameDtoMapper::mapToGameDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/turn")
    public ResponseEntity<PlayerDto> getTurn(@Valid @RequestBody PlayerMoveRequest req) {
        return gameService.managePlayerTurns(req)
                .map(player -> ResponseEntity.ok(mapToPlayerDTO(player)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /*
    Params: gameId, playerId
    Return: Tile
    Description: If it's your turn, you can pull a tile from the TilePool.
                 The tile you pulled needs to be subtracted from the TilePool and the new tile
                 also needs to be added in the player's deck.
     */
    @GetMapping("/pull-tile")
    public ResponseEntity<TileDto> getPulledTileFromTilePool(@Valid @RequestBody CreateSimpleRequest req) {
        return gameService.pullTileFromTilePool(req.gameId(), req.playerId())
                .map(tile -> ResponseEntity.ok(GameDtoMapper.mapToTileDto(tile)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", IllegalArgumentException.class.getSimpleName());
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalStateException(IllegalStateException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", IllegalStateException.class.getSimpleName());
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Map<String, String>> handleNullPointerException(NullPointerException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", NullPointerException.class.getSimpleName());
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}