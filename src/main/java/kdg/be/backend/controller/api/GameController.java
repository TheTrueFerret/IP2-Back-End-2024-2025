package kdg.be.backend.controller.api;

import jakarta.validation.Valid;
import kdg.be.backend.controller.dto.GameDto;
import kdg.be.backend.controller.dto.LobbyDto;
import kdg.be.backend.controller.dto.PlayerDto;
import kdg.be.backend.controller.dto.mapper.GameDtoMapper;
import kdg.be.backend.controller.dto.requests.CreateGameSettingsRequest;
import kdg.be.backend.domain.Player;
import kdg.be.backend.domain.Tile;
import kdg.be.backend.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
        return gameService.startGame(lobbyId, req.roundTime(), req.startTileAmount())
                .map(GameDtoMapper::mapToGameDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .build());
    }
}