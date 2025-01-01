package kdg.be.backend.service;

import kdg.be.backend.domain.Game;
import kdg.be.backend.domain.Player;
import kdg.be.backend.domain.user.GameUser;
import kdg.be.backend.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GameTurnService {
    private final PlayerRepository playerRepository;

    private static final Logger log = LoggerFactory.getLogger(GameTurnService.class);

    public GameTurnService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    private List<UUID> randomizePlayerTurns(Game game) {
        List<UUID> randomizedPlayersIds = new ArrayList<>();

        for (Player player : game.getPlayers()) {
            randomizedPlayersIds.add(player.getId());
        }

        Collections.shuffle(randomizedPlayersIds);
        game.setPlayerTurnOrder(randomizedPlayersIds);

        String playerNames = randomizedPlayersIds.stream()
                .map(playerRepository::findPlayerById)
                .map(optionalPlayer -> optionalPlayer.map(Player::getGameUser)
                        .map(GameUser::getUsername)
                        .orElse("Unknown"))
                .collect(Collectors.joining(", "));
        log.info("Player turn order initialized for game with players: {}", playerNames);

        return randomizedPlayersIds;
    }

    public void initializePlayerTurns(Game game, List<Player> players) {
        List<UUID> randomizedPlayersIds = randomizePlayerTurns(game);

        // Set the turn start and end times for the first player
        players.forEach(player -> {
            if (playerRepository.findPlayerInGameByGameIdAndPlayerId(game.getId(), player.getId()).isPresent()) {
                player.setTurnStartTime(LocalDateTime.now());
                player.setTurnEndTime(LocalDateTime.now().plusSeconds(game.getTurnTime()));
            }
            playerRepository.save(player);
        });

        // Log the first turn start and end times
        playerRepository.findPlayerInGameByGameIdAndPlayerId(
                        game.getId(),
                        randomizedPlayersIds.getFirst())
                .ifPresentOrElse(player -> log.info("First turn for player: {} starts at {} and ends at (+{} seconds) {}", player.getGameUser().getUsername(), player.getTurnStartTime(), game.getTurnTime(), player.getTurnEndTime()),
                        () -> log.error("No player found for the first turn with gameId {} and playerId {}", game.getId(), randomizedPlayersIds.getFirst())
                );
    }
}
