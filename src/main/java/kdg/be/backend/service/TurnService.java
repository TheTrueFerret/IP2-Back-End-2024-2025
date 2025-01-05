package kdg.be.backend.service;

import kdg.be.backend.controller.dto.requests.PlayerMoveTileDto;
import kdg.be.backend.controller.dto.requests.PlayerMoveTileDto;
import kdg.be.backend.controller.dto.requests.PlayerMoveTileSetDto;
import kdg.be.backend.domain.*;
import kdg.be.backend.domain.enums.GameState;
import kdg.be.backend.exception.TileSetException;
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
public class TurnService {
    // repositories
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final DeckRepository deckRepository;
    private final TileRepository tileRepository;
    private final TilePoolRepository tilePoolRepository;

    // services
    private final PlayingFieldService playingFieldService;
    private final MoveValidationService moveValidationService;
    private final GameUserAchievementService gameUserAchievementService;
    private final PlayerService playerService;

    // other
    private static final Logger log = LoggerFactory.getLogger(TurnService.class);

    public TurnService(GameRepository gameRepository, PlayerRepository playerRepository, DeckRepository deckRepository,
                       TileRepository tileRepository, TilePoolRepository tilePoolRepository, PlayingFieldService playingFieldService,
                       MoveValidationService moveValidationService, GameUserAchievementService gameUserAchievementService,
                       PlayerService playerService) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.deckRepository = deckRepository;
        this.tileRepository = tileRepository;
        this.tilePoolRepository = tilePoolRepository;
        this.playingFieldService = playingFieldService;
        this.moveValidationService = moveValidationService;
        this.gameUserAchievementService = gameUserAchievementService;
        this.playerService = playerService;
    }

    private void managePlayerTurns(Player player, List<UUID> playerTurnOrders) {
        if (!player.getId().equals(playerTurnOrders.getFirst())) {
            throw new IllegalStateException(player.getGameUser().getUsername() + ": it's not your turn, wait until its your turn to make a move");
        }
    }

    @Transactional
    public void managePlayerMoves(UUID playerId, UUID gameId, List<PlayerMoveTileSetDto> tileSetDtos, List<PlayerMoveTileDto> deckDto) {
        Game game = gameRepository.findGameById(gameId)
                .orElseThrow(() -> new NullPointerException("Game not found"));

        if (game.getGameState() == GameState.ENDED) {
            throw new IllegalStateException("Game has already ended! You can't make a move anymore.");
        }

        Player player = playerRepository.findPlayerInGameByGameIdAndPlayerId(gameId, playerId)
                .orElseThrow(() -> new NullPointerException("Player trying to play not found"));

        List<UUID> playerTurnOrders = gameRepository.findPlayerTurnOrdersByGameId(gameId)
                .orElseThrow(() -> new NullPointerException("Player turn orders not found"));

        managePlayerTurns(player, playerTurnOrders);

        if (LocalDateTime.now().isAfter(player.getTurnStartTime()) && LocalDateTime.now().isBefore(player.getTurnEndTime())) {
            player.setTurnMoveTime(LocalDateTime.now());
            makePlayerMove(player, gameId, tileSetDtos, deckDto);
            log.info("Player with id {}, has score {}", player.getId(), player.getScore());
        } else {
            log.warn("{} didn't make a move when it was their turn from {} to {}. Move was made at {}"
                    , player.getGameUser().getUsername(), player.getTurnStartTime(), player.getTurnEndTime(), player.getTurnMoveTime());
        }

        getNextPlayer(playerTurnOrders, game, player);
    }

    private void getNextPlayer(List<UUID> turnOrders, Game game, Player currentPlayer) {
        if (game.getGameState() != GameState.ENDED) {
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
                        nextPlayer.setTurnStartTime(LocalDateTime.now());
                        nextPlayer.setTurnEndTime(nextPlayer.getTurnStartTime().plusSeconds(game.getTurnTime()));
                        playerRepository.save(nextPlayer);
                    });

            log.info("The next turn is for player: {}, make a move within the time limit: from {} to {}",
                    nextPlayer.getGameUser().getUsername(), nextPlayer.getTurnStartTime(), nextPlayer.getTurnEndTime());
        }
    }

    private void makePlayerMove(Player player, UUID gameId, List<PlayerMoveTileSetDto> tileSetDtos, List<PlayerMoveTileDto> deckDto) {
        // First turn check
        checkFirstTurn(player, deckDto);

        try {
            for (PlayerMoveTileSetDto tileSetDto : tileSetDtos) {
                moveValidationService.checkTileSet(tileSetDto.tiles());
            }
        } catch (TileSetException e) {
            throw new IllegalArgumentException("Player" + player.getGameUser().getUsername() + " made an invalid move:" + e.getMessage() + " Skipping to next player");
        }

        // Handle player moves
        playingFieldService.handlePlayerMoves(gameId, tileSetDtos);
        playingFieldService.handlePlayerDeck(player.getId(), deckDto);
        log.info("Player {} made a move within the time limit: from {} to {}, move was made at {}",
                player.getGameUser().getUsername(), player.getTurnStartTime(), player.getTurnEndTime(),
                LocalTime.now());

        // Checks
        gameUserAchievementService.checkAndAssignFirstMoveAchievement(player.getGameUser().getId());
        gameUserAchievementService.checkAndAssignParticipationAchievement(player.getGameUser().getId());

        // Check if the player has no tiles left in their deck
        playerService.checkPlayerTiles(player);
    }

    private void checkFirstTurn(Player player, List<PlayerMoveTileDto> deckDto) {
        Game game = player.getGame();
        if (!game.getPlayerTurnHistory().contains(player.getId())) {
            moveValidationService.isValidInitialMove(player.getId(), deckDto);
            game.getPlayerTurnHistory().add(player.getId());
            gameRepository.save(game);
        }
    }

    @Transactional
    public Optional<Tile> managePullingTileFromTilePool(UUID gameId, UUID playerId) {
        Optional<Tile> drawnTileFromTilePool = Optional.empty();

        List<UUID> playerTurnOrders = gameRepository.findPlayerTurnOrdersByGameId(gameId)
                .orElseThrow(() -> new IllegalStateException("Player turn orders not found"));

        Player player = playerRepository.findPlayerInGameByGameIdAndPlayerId(gameId, playerId)
                .orElseThrow(() -> new NullPointerException("Player trying to play not found"));

        // Haal de game object en de tile pool op die niet tot een deck behoort
        Game game = gameRepository.findGameByIdWithTilePoolTilesWithDeckIsNull(gameId)
                .orElseThrow(() -> new IllegalStateException("No tiles left in the tilepool of the game"));

        if (game.getGameState() == GameState.ENDED) {
            throw new IllegalStateException("Game has already ended! You can't pull a tile anymore.");
        }

        if (LocalDateTime.now().isAfter(player.getTurnStartTime()) && LocalDateTime.now().isBefore(player.getTurnEndTime())) {
            drawnTileFromTilePool = drawTileFromTilePool(player, playerTurnOrders, game);
            playerService.calculateNoTilesInTilePoolPlayerScores(game);
        } else {
            log.warn("{} didn't pull a tile when it was their turn from {} to {}. Move was made at {}"
                    , player.getGameUser().getUsername(), player.getTurnStartTime(), player.getTurnEndTime(), LocalTime.now());
        }

        // Na de beurt moet het geupdatet worden zodat de volgende speler aan de beurt geraakt
        getNextPlayer(playerTurnOrders, game, player);

        return drawnTileFromTilePool;
    }

    private Optional<Tile> drawTileFromTilePool(Player player, List<UUID> playerTurnOrders, Game game) {
        // Check of je aan de beurt bent
        managePlayerTurns(player, playerTurnOrders);

        TilePool tilePool = game.getTilePool();
        tilePool.shuffleTiles();

        log.info("TILE POOL TILES: {}", tilePool.getTiles().size());
        log.info("TILE POOL TILE ORDER: {}", tilePool.getTiles().stream()
                .map(tile -> String.format("%d %s", tile.getNumberValue(), tile.getTileColor()))
                .collect(Collectors.joining(", ")));

        // Trek een tegel uit de pool
        Tile drawnTile = tilePool.drawTile();

        // Update de tegel en voeg deze toe aan het deck van de speler
        Deck playerDeck = player.getDeck();
        drawnTile.setDeck(playerDeck);
        playerDeck.getTiles().add(drawnTile);

        // Wijzigingen opslaan
        tileRepository.save(drawnTile);
        deckRepository.save(playerDeck);
        tilePoolRepository.save(tilePool);

        log.info("Player {} drew a tile: {} {}. Remaining tiles in pool: {}",
                player.getGameUser().getUsername(),
                drawnTile.getNumberValue(),
                drawnTile.getTileColor(),
                tilePool.getTiles().size()
        );

        String tilesInfo = tilePool.getTiles().stream()
                .map(tile -> String.format("%d %s", tile.getNumberValue(), tile.getTileColor()))
                .collect(Collectors.joining(", "));
        log.info("The tilepool has the following tiles left after player pulled a tile: {}", tilesInfo);

        String playerTilesInfo = playerDeck.getTiles().stream()
                .map(tile -> String.format("%d %s", tile.getNumberValue(), tile.getTileColor()))
                .collect(Collectors.joining(", "));
        log.info("Player {} has the following tiles in their deck after pulling a tile: {}", player.getGameUser().getUsername(), playerTilesInfo);

        return Optional.of(drawnTile);
    }
}