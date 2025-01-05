package kdg.be.backend.service;

import kdg.be.backend.domain.*;
import kdg.be.backend.domain.enums.GameState;
import kdg.be.backend.domain.user.GameUser;
import kdg.be.backend.repository.DeckRepository;
import kdg.be.backend.repository.GameRepository;
import kdg.be.backend.repository.PlayerRepository;
import kdg.be.backend.repository.TileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PlayerService {
    private final PlayerRepository playerRepository;
    private final TileRepository tileRepository;
    private final GameRepository gameRepository;
    private final DeckRepository deckRepository;

    private static final Logger log = LoggerFactory.getLogger(PlayerService.class);

    public PlayerService(PlayerRepository playerRepository, TileRepository tileRepository, GameRepository gameRepository, DeckRepository deckRepository) {
        this.playerRepository = playerRepository;
        this.tileRepository = tileRepository;
        this.gameRepository = gameRepository;
        this.deckRepository = deckRepository;
    }

    public UUID getPlayerIdByUserId(UUID userId) {
        return playerRepository.findPlayerByUserId(userId, GameState.ONGOING)
                .map(Player::getId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found with userId: " + userId));
    }

    public List<Tile> getDeckTilesOfPlayer(UUID playerId) {
        return tileRepository.findDeckTilesByPlayerId(playerId)
                .filter(tiles -> !tiles.isEmpty())
                .orElseThrow(() -> new IllegalArgumentException("Couldn't find the deck tiles of player with ID: " + playerId));
    }

    public int getPlayerScore(UUID playerId) {
        return playerRepository.findById(playerId)
                .map(Player::getScore)
                .orElseThrow(() -> new IllegalArgumentException("Player not found with ID: " + playerId));
    }

    public Player getCurrentTurnPlayer(UUID gameId) {
        Game game = gameRepository.findByGameId(gameId).orElseThrow(() -> new IllegalArgumentException("Game not found with ID: " + gameId));

        if (game.getGameState() == GameState.ENDED) {
            throw new IllegalStateException("Game has ended");
        }

        List<UUID> playerTurnOrders = gameRepository.findPlayerTurnOrdersByGameId(gameId)
                .filter(orderList -> !orderList.isEmpty())
                .orElseThrow(() -> new NullPointerException("Player turn orders not found"));

        return playerRepository.findPlayerById(playerTurnOrders.getFirst())
                .orElseThrow(() -> new IllegalStateException("Couldn't retrieve first player turn"));
    }

    public List<Player> getPlayersOfGame(UUID gameId) {
        return playerRepository.findPlayersByGameId(gameId)
                .filter(list -> !list.isEmpty())
                .orElseThrow(() -> new IllegalArgumentException("Couldn't find players of game with ID: " + gameId));
    }

    private Deck createPlayerDeck(TilePool tilePool, int tilesPerPlayer) {
        List<Tile> playerTiles = new ArrayList<>();

        for (int i = 0; i < tilesPerPlayer; i++) {
            playerTiles.add(tilePool.drawTile());
        }
        return new Deck(playerTiles);
    }

    public List<Player> createPlayers(Lobby lobby, TilePool tilePool, int startTileAmount, Game game, List<Tile> tiles) {
        List<Player> players = new ArrayList<>();

        for (GameUser user : lobby.getUsers()) {
            // Create a deck for the player
            Deck playerDeck = createPlayerDeck(tilePool, startTileAmount);
            deckRepository.save(playerDeck);

            // Set the deck for each tile
            playerDeck.getTiles().forEach(tile -> tile.setDeck(playerDeck));
            tileRepository.saveAll(tiles);

            // Create a player and save it and add it to the list of players
            Player player = new Player(user, playerDeck, game);
            players.add(player);
            playerRepository.save(player);

            String tilesInfo = playerDeck.getTiles().stream()
                    .map(tile -> String.format("%d %s", tile.getNumberValue(), tile.getTileColor()))
                    .collect(Collectors.joining(", "));
            log.info("Player {} has the following tiles in their deck: {}", user.getUsername(), tilesInfo);
        }
        return players;
    }

    public int getCurrentTurnTime(UUID playerId) {
        Player player = playerRepository.findPlayerById(playerId)
                .orElseThrow(() -> new NullPointerException("Player trying to play not found"));

        if (!LocalDateTime.now().isAfter(player.getTurnStartTime()) && !LocalDateTime.now().isBefore(player.getTurnEndTime())) {

            log.warn("{} didn't pull a tile when it was their turn from {} to {}. Move was made at {}"
                    , player.getGameUser().getUsername(), player.getTurnStartTime(), player.getTurnEndTime(), LocalTime.now());
        }
        return (int) Duration.between(LocalTime.now(), player.getTurnEndTime()).toSeconds();
    }


    /**
     * Check if the player has won the game
     * (= if the player has no tiles left in his deck)
     * TODO add achievement for winning the game
     */
    public void checkPlayerTiles(Player winner) {
        if (winner.getDeck().getTiles().isEmpty()) {
            Game game = winner.getGame();
            game.setGameState(GameState.ENDED);
            gameRepository.save(game);

            log.info("Game has ended, {} is the first player to place all their tiles on the playing field!", winner.getGameUser().getUsername());
            calculatePlayerScores(game, winner);
        }
    }

    /*
     * Calculate the scores of the players at the end of the game
     * (If player wins = the game ends so calculate all the players their scores)
     */
    public void calculatePlayerScores(Game game, Player winner) {
        int winnerScore = 0;

        if (game.getGameState() == GameState.ENDED) {
            for (Player player : game.getPlayers()) {
                if (!winner.equals(player)) {
                    int score = player.getDeck().getTiles().stream()
                            .mapToInt(Tile::getNumberValue)
                            .sum();

                    player.setScore(-score);
                    winnerScore += score;

                    playerRepository.save(player);
                }
            }

            winner.setScore(winnerScore);
            playerRepository.save(winner);
            setGameLeaderboard(game);
        }
    }

    public void calculateNoTilesInTilePoolPlayerScores(Game game) {
        if (game.getTilePool().isEmpty()) {

            game.setGameState(GameState.ENDED);
            gameRepository.save(game);
            log.info("Game has ended, no tiles left in the tilepool!");

            for (Player playerScore : game.getPlayers()) {
                int score = playerScore.getDeck().getTiles().stream()
                        .mapToInt(Tile::getNumberValue)
                        .sum();

                playerScore.setScore(-score);
                playerRepository.save(playerScore);
            }

            setGameLeaderboard(game);
        }

    }

    public void setGameLeaderboard(Game game) {
        if (game.getGameState() != GameState.ENDED) {
            throw new IllegalStateException("Game is not finished yet, cannot create leaderboard");
        }

        List<Player> players = game.getPlayers();
        players.sort(Comparator.comparingInt(Player::getScore).reversed());

        List<UUID> playerIds = players.stream()
                .map(Player::getId)
                .collect(Collectors.toList());
        game.setPlayerLeaderboard(playerIds);
        gameRepository.save(game);

        log.info("Leaderboard for game with id: {}", game.getId());
        players.forEach(player -> log.info("Player: {} has {} tiles left with a score of {}",
                player.getGameUser().getUsername(), player.getDeck().getTiles().size(), player.getScore()));
    }
}