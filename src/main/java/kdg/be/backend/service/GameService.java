package kdg.be.backend.service;

import kdg.be.backend.domain.*;
import kdg.be.backend.domain.enums.LobbyStatus;
import kdg.be.backend.domain.user.GameUser;
import kdg.be.backend.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GameService {
    private final TileRepository tileRepository;
    private final LobbyRepository lobbyRepository;
    private final PlayingFieldRepository playingFieldRepository;
    private final GameRepository gameRepository;
    private final TilePoolRepository tilePoolRepository;
    private final TileService tileService;
    private final PlayerService playerService;
    private final GameTurnService gameTurnService;

    private static final Logger log = LoggerFactory.getLogger(GameService.class);

    public GameService(TileRepository tileRepository, LobbyRepository lobbyRepository, PlayingFieldRepository playingFieldRepository, GameRepository gameRepository, TilePoolRepository tilePoolRepository, TileService tileService, PlayerService playerService, GameTurnService gameTurnService) {
        this.tileRepository = tileRepository;
        this.lobbyRepository = lobbyRepository;
        this.playingFieldRepository = playingFieldRepository;
        this.gameRepository = gameRepository;
        this.tilePoolRepository = tilePoolRepository;
        this.tileService = tileService;
        this.playerService = playerService;
        this.gameTurnService = gameTurnService;
    }

    private void validateGameStart(Lobby lobby, UUID hostUserId) {
        if (!lobby.getHostUser().getId().equals(hostUserId)) {
            log.error("Only the host of the lobby can start the game!");
            throw new IllegalStateException("Only the host of the lobby can start the game!");
        }

        lobby.setStatus(LobbyStatus.READY);
        lobbyRepository.save(lobby);

        if (lobby.getStatus() != LobbyStatus.READY) {
            log.error("Cannot start game if lobby is not started.");
            throw new IllegalStateException("Cannot start game if lobby is not started.");
        }

        if (gameRepository.countGamesByLobbyId(lobby.getId()) > 1) {
            log.error("There can only exist 1 game instance for every lobby");
            throw new IllegalStateException("There can only exist 1 game instance for every lobby");
        }
    }

    @Transactional
    public Optional<Game> startGame(UUID lobbyId, int roundTime, int startTileAmount, UUID hostUserId) {
        return lobbyRepository.findLobbyById(lobbyId)
                .map(lobby -> {
                    // Validate if the lobby is ready to start the game
                    validateGameStart(lobby, hostUserId);

                    // Create tiles
                    List<Tile> tiles = tileService.createTiles(startTileAmount);
                    tileRepository.saveAll(tiles);

                    // Create a tile pool and shuffle the tiles
                    TilePool tilePool = new TilePool(tiles);
                    tilePool.shuffleTiles();

                    // Save the tile pool
                    tiles.forEach(tile -> tile.setTilePool(tilePool));
                    tileRepository.saveAll(tiles);

                    // Create a playing field
                    PlayingField playingField = new PlayingField(new ArrayList<>());
                    playingFieldRepository.save(playingField);

                    // Create a game and save it
                    Game game = new Game(
                            roundTime,
                            startTileAmount,
                            LocalDateTime.now(),
                            playingField,
                            tilePool,
                            new ArrayList<>(),
                            lobby
                    );
                    gameRepository.save(game);

                    // Create players
                    List<Player> players = playerService.createPlayers(lobby, tilePool, startTileAmount, game, tiles);

                    log.info("TILES LEFT OVER AFTER GIVING EACH PLAYER {}: {}", startTileAmount, tilePool.getTiles().size());
                    String tilesInfo = tilePool.getTiles().stream()
                            .map(tile -> String.format("%d %s", tile.getNumberValue(), tile.getTileColor()))
                            .collect(Collectors.joining(", "));
                    log.info("The tilepool has the following tiles left: {}", tilesInfo);

                    tilePoolRepository.save(tilePool);
                    game.setPlayers(players);

                    // Validate if all players have the same amount of tiles
                    tileService.validateEqualTileCounts(players, startTileAmount);

                    // Initialize player turns
                    gameTurnService.initializePlayerTurns(game, players);
                    gameRepository.save(game);

                    log.info("Game started with lobby id: {}", lobbyId);
                    return game;
                });
    }


    public Optional<UUID> getGameIdByLobbyIdAndUserId(UUID lobbyId, UUID userId) {
        Lobby lobby = lobbyRepository.findLobbyById(lobbyId)
                .orElseThrow(() -> new IllegalStateException("No lobby found with id: " + lobbyId));

        Optional<UUID> gameId = gameRepository.findGameByLobbyId(lobbyId).map(Game::getId);

        if (gameId.isEmpty()) {
            log.error("no Game Found for the LobbyId: {}", lobbyId);
            throw new IllegalArgumentException("No Game Found for the LobbyId: " + lobbyId);
        }

        boolean isPlayer = lobby.getUsers().stream().anyMatch(user -> user.getId().equals(userId));

        if (!isPlayer) {
            log.error("user is not a part of this Lobby: {}", lobbyId);
            throw new IllegalArgumentException("user is not a part of this Lobby: " + lobbyId);
        }
        return gameId;
    }


    public Optional<UUID> getGameIdByPlayerId(UUID playerId) {
        Optional<UUID> gameId = gameRepository.findGameByPlayerId(playerId).map(Game::getId);

        if (gameId.isEmpty()) {
            log.error("no Game Found for the PlayerId: {}", playerId);
            throw new IllegalArgumentException("No Game Found for the PlayerId: " + playerId);
        }
        return gameId;
    }

    @Transactional
    public Boolean leaveGame(UUID playerId) {
        Game game = gameRepository.findGameByPlayerId(playerId)
                .orElseThrow(() -> new IllegalArgumentException(""));

        if (!removePlayerFromGame(game.getId(), playerId)) {
            throw new IllegalArgumentException("Player is not in this game");
        }
        return true;
    }

    @Transactional
    public boolean removePlayerFromGame(UUID gameId, UUID playerId) {
        Game game = gameRepository.findByGameId(gameId)
                .orElseThrow(() -> new IllegalArgumentException("game does not exist"));

        List<Player> playersInGame = game.getPlayers();
        boolean isPlayerPresent = playersInGame.stream()
                .anyMatch(playerInGame -> playerInGame.getId().equals(playerId));

        if (!isPlayerPresent) {
            throw new IllegalArgumentException("Player Not in This Game");
        }

        playersInGame.removeIf(player -> player.getId().equals(playerId));

        if (playersInGame.isEmpty()) {
            gameRepository.delete(game);
            // maybe not fully deleted but ait
        } else {
            game.setPlayers(playersInGame);
            gameRepository.save(game);
        }
        return true;
    }
}