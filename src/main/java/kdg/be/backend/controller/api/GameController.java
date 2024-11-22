package kdg.be.backend.controller.api;

import kdg.be.backend.controller.dto.PlayerDTO;
import kdg.be.backend.domain.Player;
import kdg.be.backend.domain.Tile;
import kdg.be.backend.service.GameService;
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

    @GetMapping("/tiles/player/{playerId}")
    public List<Tile> getTilesOfPlayer(@PathVariable UUID playerId) {
        return gameService.getTilesOfPlayer(playerId);
    }

    @GetMapping("/players/{gameId}")
    @Transactional
    public List<PlayerDTO> getPlayersOfGame(@PathVariable UUID gameId) {
        List<Player> players = gameService.getPlayersOfGame(gameId);
        return mapToPlayerDTOs(players);
    }

    private List<PlayerDTO> mapToPlayerDTOs(List<Player> players) {
        return players.stream()
                .map(player -> new PlayerDTO(
                        player.getId(),
                        player.getGameUser().getUsername(), // Assuming getGameUser() fetches the username
                        player.getGame().getId()            // Assuming getGame() fetches the game ID
                ))
                .collect(Collectors.toList());
    }
}
