package kdg.be.backend.service;

import kdg.be.backend.domain.*;
import kdg.be.backend.repository.DeckRepository;
import kdg.be.backend.repository.GameRepository;
import kdg.be.backend.repository.PlayerRepository;
import kdg.be.backend.repository.TileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
        return playerRepository.findPlayerByUserId(userId)
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
}
