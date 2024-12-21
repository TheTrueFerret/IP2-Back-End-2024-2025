package kdg.be.backend.controller.api;

import kdg.be.backend.controller.dto.player.PlayerDto;
import kdg.be.backend.controller.dto.player.PlayerScoreReturnDto;
import kdg.be.backend.controller.dto.mapper.GameDtoMapper;
import kdg.be.backend.controller.dto.tiles.TileDto;
import kdg.be.backend.domain.Player;
import kdg.be.backend.service.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static kdg.be.backend.controller.dto.mapper.GameDtoMapper.mapToPlayerDto;

@RestController
@RequestMapping("/api/players")
public class PlayerController {
    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
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

    // TODO Add Tests for this method
    @GetMapping("/{userId}")
    public UUID getPlayerIdByUserId(@PathVariable UUID userId) {
        return playerService.getPlayerIdByUserId(userId);
    }

    @GetMapping("/tiles/{playerId}")
    public List<TileDto> getDeckTilesOfPlayer(@PathVariable UUID playerId) {
        return playerService.getDeckTilesOfPlayer(playerId)
                .stream()
                .map(GameDtoMapper::mapToTileDto)
                .toList();
    }

    @GetMapping("/game/{gameId}/turns/current-player-turn")
    public PlayerDto getCurrentPlayerTurn(@PathVariable UUID gameId) {
        return mapToPlayerDto(playerService.getCurrentTurnPlayer(gameId));
    }

    @GetMapping("/game/{gameId}")
    public List<PlayerDto> getPlayersOfGame(@PathVariable UUID gameId) {
        List<Player> players = playerService.getPlayersOfGame(gameId);
        return mapToPlayerDTOs(players);
    }

    @GetMapping("/{playerId}/score")
    public ResponseEntity<PlayerScoreReturnDto> getPlayerScore(@PathVariable UUID playerId) {
        int score = playerService.getPlayerScore(playerId);
        return ResponseEntity.ok(new PlayerScoreReturnDto(playerId, score));
    }
}
