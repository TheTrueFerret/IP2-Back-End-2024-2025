package kdg.be.backend.service;

import kdg.be.backend.domain.*;
import kdg.be.backend.domain.enums.LobbyStatus;
import kdg.be.backend.domain.enums.TileColor;
import kdg.be.backend.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GameService {
    private final TileRepository tileRepository;
    private final PlayerRepository playerRepository;
    private final LobbyRepository lobbyRepository;
    private final PlayingFieldRepository playingFieldRepository;
    private final GameRepository gameRepository;
    private final DeckRepository deckRepository;

    private static final Logger log = LoggerFactory.getLogger(LobbyService.class);

    public GameService(TileRepository tileRepository, PlayerRepository playerRepository, LobbyRepository lobbyRepository, PlayingFieldRepository playingFieldRepository, GameRepository gameRepository, DeckRepository deckRepository) {
        this.tileRepository = tileRepository;
        this.playerRepository = playerRepository;
        this.lobbyRepository = lobbyRepository;
        this.playingFieldRepository = playingFieldRepository;
        this.gameRepository = gameRepository;
        this.deckRepository = deckRepository;
    }

    public List<Tile> getTilesOfPlayer(UUID playerId) {
        return tileRepository.findTilesByPlayerId(playerId);
    }

    public List<Player> getPlayersOfGame(UUID gameId) {
        return playerRepository.findPlayersByGameId(gameId);
    }

    private List<Tile> createTiles(int amount) {
        List<Tile> tiles = new ArrayList<>();
        TileColor[] colors = TileColor.values();

        for (int number = 1; number <= 13; number++) {
            for (TileColor color : colors) {
                tiles.add(new Tile(number, color));
                tiles.add(new Tile(number, color)); // Dubbele set per kleur en nummer
            }
        }

        tiles.add(new Tile(0, TileColor.BLACK)); // Joker 1
        tiles.add(new Tile(0, TileColor.RED)); // Joker 2

        log.info("AMOUNT OF TILES GENERATED: {}", tiles.size());

        if (tiles.size() < amount) {
            throw new IllegalArgumentException("Not enough tiles to create a pool");
        }

        return tiles;
    }

    private Deck createPlayerDeck(TilePool tilePool, int tilesPerPlayer) {
        List<Tile> playerTiles = new ArrayList<>();

        for (int i = 0; i < tilesPerPlayer; i++) {
            playerTiles.add(tilePool.drawTile());
        }

        return new Deck(playerTiles);
    }

    @Transactional
    public Optional<Game> startGame(UUID lobbyId, int roundTime, int startTileAmount) {
        try {
            return lobbyRepository.findLobbyById(lobbyId)
                    .map(lobby -> {
                        if (lobby.getStatus() != LobbyStatus.READY) {
                            throw new IllegalStateException("Cannot start game if lobby is not started.");
                        }

                        List<Tile> tiles = createTiles(startTileAmount);
                        tileRepository.saveAll(tiles);
                        TilePool tilePool = new TilePool(tiles);
                        tilePool.shuffleTiles();


                        PlayingField playingField = new PlayingField(new ArrayList<>());
                        playingFieldRepository.save(playingField);


                        Game game = new Game(
                                roundTime,
                                startTileAmount,
                                LocalDateTime.now(),
                                playingField,
                                tilePool,
                                new ArrayList<>()
                        );


                        List<Player> players = new ArrayList<>();
                        for (GameUser user : lobby.getUsers()) {
                            Deck playerDeck = createPlayerDeck(tilePool, startTileAmount);
                            deckRepository.save(playerDeck);

                            Player player = new Player(user, playerDeck, game);
                            players.add(player);
                            playerRepository.save(player);
                        }

                        game.setPlayers(players);
                        validateEqualTileCounts(players, startTileAmount);
                        gameRepository.save(game);

                        log.info("Game started with lobby id: {}", lobbyId);
                        return game;
                    });
        } catch (IllegalStateException e) {
            log.error("Game could not start: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private void validateEqualTileCounts(List<Player> players, int startTileAmount) {
        for (Player player : players) {
            int playerTileCount = player.getDeck().getTiles().size();
            if (playerTileCount != startTileAmount) {
                throw new IllegalStateException("Tile distribution error: Players do not have equal tile counts.");
            }
        }

        log.info("All players have equal tile counts: {}", startTileAmount);
    }
}
