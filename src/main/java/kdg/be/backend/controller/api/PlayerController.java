package kdg.be.backend.controller.api;

import kdg.be.backend.controller.dto.player.PlayerDto;
import kdg.be.backend.controller.dto.player.PlayerScoreReturnDto;
import kdg.be.backend.controller.dto.mapper.GameDtoMapper;
import kdg.be.backend.controller.dto.player.SimplePlayerDto;
import kdg.be.backend.controller.dto.tiles.TileDto;
import kdg.be.backend.domain.Player;
import kdg.be.backend.service.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static kdg.be.backend.controller.dto.mapper.GameDtoMapper.mapToPlayerDto;
import static kdg.be.backend.controller.dto.mapper.PlayerMapper.mapToSimplePlayerDtos;

@RestController
@RequestMapping("/api/players")
public class PlayerController {
    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    // TODO Add Tests for this method
    @GetMapping("/{userId}")
    public UUID getPlayerIdByUserId(@PathVariable UUID userId) {
        return playerService.getPlayerIdByUserId(userId);
    }

    @GetMapping("/tiles/{playerId}")
    public ResponseEntity<List<TileDto>> getDeckTilesOfPlayer(@PathVariable UUID playerId) {
        var playerDeckTiles = playerService.getDeckTilesOfPlayer(playerId)
                .stream()
                .map(GameDtoMapper::mapToTileDto)
                .toList();

        return ResponseEntity.ok(playerDeckTiles);
    }

    @GetMapping("/game/{gameId}/turns/current-player-turn")
    public ResponseEntity<PlayerDto> getCurrentPlayerTurn(@PathVariable UUID gameId) {
        return ResponseEntity.ok(mapToPlayerDto(playerService.getCurrentTurnPlayer(gameId)));
    }

    @GetMapping("/game/{gameId}")
    public ResponseEntity<List<SimplePlayerDto>> getPlayersOfGame(@PathVariable UUID gameId) {
        List<Player> players = playerService.getPlayersOfGame(gameId);
        return ResponseEntity.ok(mapToSimplePlayerDtos(players));
    }

    @GetMapping("/{playerId}/score")
    public ResponseEntity<Integer> getPlayerScore(@PathVariable UUID playerId) {
        int score = playerService.getPlayerScore(playerId);
        return ResponseEntity.ok(score);
    }

    // TODO add TEST
    @GetMapping("/time/{playerId}")
    public ResponseEntity<Integer> getCurrentTurnTime(@PathVariable UUID playerId) {
        int time = playerService.getCurrentTurnTime(playerId);
        return ResponseEntity.ok(time);
    }
}