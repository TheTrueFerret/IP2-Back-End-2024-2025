package kdg.be.backend.service;

import kdg.be.backend.controller.dto.requests.PlayerMoveDeckDto;
import kdg.be.backend.controller.dto.requests.PlayerMoveTileSetDto;
import kdg.be.backend.domain.*;
import kdg.be.backend.domain.enums.LobbyStatus;
import kdg.be.backend.domain.enums.TileColor;
import kdg.be.backend.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GameService {
    private final TileRepository tileRepository;
    private final PlayerRepository playerRepository;
    private final LobbyRepository lobbyRepository;
    private final PlayingFieldRepository playingFieldRepository;
    private final GameRepository gameRepository;
    private final DeckRepository deckRepository;
    private final PlayingFieldService playingFieldService;

    private static final Logger log = LoggerFactory.getLogger(GameService.class);

    public GameService(TileRepository tileRepository, PlayerRepository playerRepository, LobbyRepository lobbyRepository, PlayingFieldRepository playingFieldRepository, GameRepository gameRepository, DeckRepository deckRepository, PlayingFieldService playingFieldService) {
        this.tileRepository = tileRepository;
        this.playerRepository = playerRepository;
        this.lobbyRepository = lobbyRepository;
        this.playingFieldRepository = playingFieldRepository;
        this.gameRepository = gameRepository;
        this.deckRepository = deckRepository;
        this.playingFieldService = playingFieldService;
    }

    public UUID getPlayerIdByUserId(UUID userId) {
        return playerRepository.findPlayerByUserId(userId)
                .map(Player::getId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found with userId: " + userId));
    }


    public List<Tile> getDeckTilesOfPlayer(UUID playerId) {
        return tileRepository.findDeckTilesByPlayerId(playerId);
    }

//    public Player getCurrentTurnPlayer(UUID gameId, UUID playerId) {
//        return gameRepository.
//    }

    @Transactional
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
    public Optional<Game> startGame(UUID lobbyId, int roundTime, int startTileAmount, UUID hostUserId) {
        return lobbyRepository.findLobbyById(lobbyId)
                .map(lobby -> {
                    if (!lobby.getHostUser().getId().equals(hostUserId)) {
                        log.error("Only the host of the lobby can start the game!");
                        throw new IllegalStateException("Only the host of the lobby can start the game!");
                    }

                    // zet lobby op ready wanneer je een game gaat starten
                    lobby.setStatus(LobbyStatus.READY);
                    lobbyRepository.save(lobby);

                    if (lobby.getStatus() != LobbyStatus.READY) {
                        log.error("Cannot start game if lobby is not started.");
                        throw new IllegalStateException("Cannot start game if lobby is not started.");
                    }

                    if (gameRepository.countGamesByLobbyId(lobbyId) > 1) {
                        log.error("There can only exist 1 game instance for every lobby");
                        throw new IllegalStateException("There can only exist 1 game instance for every lobby");
                    }

                    List<Tile> tiles = createTiles(startTileAmount);
                    tileRepository.saveAll(tiles);
                    TilePool tilePool = new TilePool(tiles);
                    tilePool.shuffleTiles();

                    tiles.forEach(tile -> tile.setTilePool(tilePool));
                    tileRepository.saveAll(tiles);

                    PlayingField playingField = new PlayingField(new ArrayList<>());
                    playingFieldRepository.save(playingField);

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

                    List<Player> players = new ArrayList<>();
                    for (GameUser user : lobby.getUsers()) {
                        Deck playerDeck = createPlayerDeck(tilePool, startTileAmount);
                        deckRepository.save(playerDeck);

                        playerDeck.getTiles().forEach(tile -> tile.setDeck(playerDeck));
                        tileRepository.saveAll(tiles);

                        Player player = new Player(user, playerDeck, game);
                        players.add(player);
                        playerRepository.save(player);
                    }

                    game.setPlayers(players);
                    validateEqualTileCounts(players, startTileAmount);
                    initializePlayerTurns(game, players);
                    gameRepository.save(game);

                    log.info("Game started with lobby id: {}", lobbyId);
                    return game;
                });
    }


    public Optional<UUID> getGameIdByLobbyIdAndUserId(UUID lobbyId, UUID userId) {
        Lobby lobby = lobbyRepository.findLobbyById(lobbyId)
                .orElseThrow(() -> new IllegalStateException("No lobby found with id: " + lobbyId));

        if (gameRepository.countGamesByLobbyId(lobbyId) > 1) {
            log.error("There can only exist 1 game instance for every lobby");
            throw new IllegalStateException("There can only exist 1 game instance for every lobby");
        }

        if (lobby.getStatus() != LobbyStatus.READY) {
            log.error("Cannot start game if lobby is not started.");
            throw new IllegalStateException("Cannot start game if lobby is not started.");
        }

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

    private void validateEqualTileCounts(List<Player> players, int startTileAmount) {
        for (Player player : players) {
            int playerTileCount = player.getDeck().getTiles().size();
            if (playerTileCount != startTileAmount) {
                throw new IllegalStateException("Tile distribution error: Players do not have equal tile counts.");
            }
        }

        log.info("All players have equal tile counts: {}", startTileAmount);
    }

    private void initializePlayerTurns(Game game, List<Player> players) {
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

        players.forEach(player -> {
            if (playerRepository.findPlayerInGameByGameIdAndPlayerId(game.getId(), player.getId()).isPresent()) {
                player.setTurnStartTime(LocalTime.now());
                player.setTurnEndTime(LocalTime.now().plusSeconds(game.getTurnTime()));
            }
            playerRepository.save(player);
        });

        playerRepository.findPlayerInGameByGameIdAndPlayerId(
                        game.getId(),
                        randomizedPlayersIds.getFirst())
                .ifPresentOrElse(player -> log.info("First turn for player: {} starts at {} and ends at (+{} seconds) {}", player.getGameUser().getUsername(), player.getTurnStartTime(), game.getTurnTime(), player.getTurnEndTime()),
                        () -> log.error("No player found for the first turn with gameId {} and playerId {}", game.getId(), randomizedPlayersIds.getFirst())
                );
    }

    public Player getPlayerTurn(UUID gameId) {
        List<UUID> playerTurnOrders = gameRepository.findPlayerTurnOrdersByGameId(gameId)
                .orElseThrow(() -> new NullPointerException("Player turn orders not found"));

        return playerRepository.findPlayerById(playerTurnOrders.getFirst()).orElseThrow(() -> new NullPointerException("Couldn't find current player turn"));
    }

    private void managePlayerTurns(Player player, List<UUID> playerTurnOrders) {
        if (!player.getId().equals(playerTurnOrders.getFirst())) {
            throw new IllegalStateException(player.getGameUser().getUsername() + ": it's not your turn, wait until its your turn to make a move");
        }
    }

    @Transactional
    public Optional<Player> managePlayerMoves(UUID playerId, UUID gameId, List<PlayerMoveTileSetDto> tileSetDtos, PlayerMoveDeckDto deckDto) {
        return Optional.of(gameRepository.findGameById(gameId)
                .map(game -> {
                    Player player = playerRepository.findPlayerInGameByGameIdAndPlayerId(gameId, playerId)
                            .orElseThrow(() -> new NullPointerException("Player trying to play not found"));

                    List<UUID> playerTurnOrders = gameRepository.findPlayerTurnOrdersByGameId(gameId)
                            .orElseThrow(() -> new NullPointerException("Player turn orders not found"));

                    managePlayerTurns(player, playerTurnOrders);

                    if (LocalTime.now().isAfter(player.getTurnStartTime()) && LocalTime.now().isBefore(player.getTurnEndTime())) {
                        makePlayerMove(player, tileSetDtos, deckDto);
                    } else {
                        log.warn("{} didn't make a move when it was their turn from {} to {}. Move was made at {}"
                                , player.getGameUser().getUsername(), player.getTurnStartTime(), player.getTurnEndTime(), LocalTime.now());
                    }

                    return getNextPlayer(playerTurnOrders, game, player);
                }).orElseThrow(() -> new NullPointerException("Game not found")));
    }

    private Player getNextPlayer(List<UUID> turnOrders, Game game, Player currentPlayer) {
        Deque<UUID> nextPlayerTurnOrders = new ArrayDeque<>(turnOrders);

        UUID firstPlayerId = nextPlayerTurnOrders.pollFirst();
        if (firstPlayerId != null && !firstPlayerId.equals(currentPlayer.getId())) {
            throw new IllegalStateException("It is not your turn: " + currentPlayer.getGameUser().getUsername());
        }
        nextPlayerTurnOrders.offerLast(firstPlayerId);

        game.setPlayerTurnOrder(new ArrayList<>(nextPlayerTurnOrders));
        gameRepository.save(game);

        UUID nextPlayerId = nextPlayerTurnOrders.peekFirst();
        Player nextPlayer = playerRepository.findPlayerById(nextPlayerId)
                .orElseThrow(() -> new NullPointerException("Next player not found"));

        playerRepository.findPlayerInGameByGameIdAndPlayerId(game.getId(), firstPlayerId)
                .ifPresent(player -> {
                    nextPlayer.setTurnStartTime(player.getTurnEndTime());
                    nextPlayer.setTurnEndTime(nextPlayer.getTurnStartTime().plusSeconds(game.getTurnTime()));
                    playerRepository.save(nextPlayer);
                });

        log.info("The next turn is for player: {}, make a move within the time limit: from {} to {}",
                nextPlayer.getGameUser().getUsername(), nextPlayer.getTurnStartTime(), nextPlayer.getTurnEndTime());
        return nextPlayer;
    }

    private void makePlayerMove(Player player, List<PlayerMoveTileSetDto> tileSetDtos, PlayerMoveDeckDto deckDto) {
        playingFieldService.handlePlayerMoves(tileSetDtos);
        playingFieldService.handlePlayerDeck(player.getId(), deckDto);
        log.info("Player {} made a move within the time limit: from {} to {}, move was made at {}",
                player.getGameUser().getUsername(), player.getTurnStartTime(), player.getTurnEndTime(),
                LocalTime.now());
    }

    public int getPlayerScore(UUID playerId) {
        return playerRepository.findById(playerId)
                .map(Player::getScore)
                .orElseThrow(() -> new IllegalArgumentException("Player not found with ID: " + playerId));
    }

}