package kdg.be.backend.controller.api;

import jakarta.validation.Valid;
import kdg.be.backend.controller.dto.GameDto;
import kdg.be.backend.controller.dto.PlayerDto;
import kdg.be.backend.controller.dto.PlayerScoreReturnDto;
import kdg.be.backend.controller.dto.PlayingFieldDto;
import kdg.be.backend.controller.dto.mapper.GameDtoMapper;
import kdg.be.backend.controller.dto.requests.CreateGameSettingsRequest;
import kdg.be.backend.controller.dto.requests.CreateSimpleRequest;
import kdg.be.backend.controller.dto.requests.PlayerMoveRequest;
import kdg.be.backend.controller.dto.tiles.TileDto;
import kdg.be.backend.controller.dto.tiles.TilePoolDto;
import kdg.be.backend.controller.dto.tiles.TileSetDto;
import kdg.be.backend.domain.Player;
import kdg.be.backend.domain.PlayingField;
import kdg.be.backend.exception.InvalidMoveException;
import kdg.be.backend.domain.TilePool;
import kdg.be.backend.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
                        player.getGameUser().getUsername(),
                        player.getScore(),
                        player.getGame().getId(),
                        GameDtoMapper.mapToDeckDto(player.getDeck())
                ))
                .collect(Collectors.toList());
    }

    private PlayerDto mapToPlayerDTO(Player player) {
        return new PlayerDto(player.getId(), player.getGameUser().getUsername(), player.getScore(), player.getGame().getId(), GameDtoMapper.mapToDeckDto(player.getDeck()));
    }

    private TilePoolDto mapToTilePoolDto(TilePool tilePool) {
        return new TilePoolDto(
                tilePool.getTiles().stream()
                        .map(GameDtoMapper::mapToTileDto)
                        .toList()
        );
    }

    private PlayingFieldDto mapToPlayingFieldDto(PlayingField playingField){
        List<TileSetDto> tileSetDtos = playingField.getTileSets()
                .stream()
                .map(GameDtoMapper::mapToTileSetDto)
                .toList();

        return new PlayingFieldDto(tileSetDtos);
    }

    // TODO Add Tests for this method
    @GetMapping("/player/{userId}")
    public UUID getPlayerIdByUserId(@PathVariable UUID userId) {
        return gameService.getPlayerIdByUserId(userId);
    }

    @GetMapping("/tiles/player/{playerId}")
    public List<TileDto> getDeckTilesOfPlayer(@PathVariable UUID playerId) {
        return gameService.getDeckTilesOfPlayer(playerId)
                .stream()
                .map(GameDtoMapper::mapToTileDto)
                .toList();
    }

    @GetMapping("/{gameId}/turns/current-player-turn")
    public PlayerDto getCurrentPlayerTurn(@PathVariable UUID gameId) {
        return mapToPlayerDTO(gameService.getCurrentTurnPlayer(gameId));
    }

    @GetMapping("/{gameId}/playingField")
    public PlayingFieldDto getPlayingFieldByGameId(@PathVariable UUID gameId) {
        return mapToPlayingFieldDto(gameService.getPlayingFieldByGameId(gameId));
    }

    @GetMapping("/players/{gameId}")
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

    @GetMapping("/lobby/{lobbyId}")
    public ResponseEntity<UUID> getGameIdByLobbyIdAndUserId(@PathVariable UUID lobbyId, @RequestParam UUID userId) {
        return gameService.getGameIdByLobbyIdAndUserId(lobbyId, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping("/turn/player-make-move")
    public ResponseEntity<PlayerDto> makePlayerMove(@Valid @RequestBody PlayerMoveRequest req) {
        return gameService.managePlayerMoves(req.playerId(), req.gameId(), req.tileSets(), req.playerDeckDto())
                .map(player -> ResponseEntity.ok(mapToPlayerDTO(player)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PatchMapping("/pull-tile")
    public ResponseEntity<TileDto> getPulledTileFromTilePool(@Valid @RequestBody CreateSimpleRequest req) {
        return gameService.managePullingTileFromTilePool(req.gameId(), req.playerId())
                .map(tile -> ResponseEntity.ok(GameDtoMapper.mapToTileDto(tile)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/tilepool/contents")
    public ResponseEntity<TilePoolDto> getTilePoolTiles(@RequestParam UUID gameId) {
        return gameService.getTilePoolByGameId(gameId)
                .map(tilePool -> ResponseEntity.ok(mapToTilePoolDto(tilePool)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/player/{playerId}/score")
    public ResponseEntity<PlayerScoreReturnDto> getPlayerScore(@PathVariable UUID playerId) {
        int score = gameService.getPlayerScore(playerId);
        return ResponseEntity.ok(new PlayerScoreReturnDto(playerId, score));
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

    @ExceptionHandler(InvalidMoveException.class)
    public ResponseEntity<Map<String, String>> handleInvalidMoveException(InvalidMoveException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", InvalidMoveException.class.getSimpleName());
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}