package kdg.be.backend.service;

import kdg.be.backend.controller.dto.requests.PlayerMoveTileDto;
import kdg.be.backend.controller.dto.requests.PlayerMoveTileSetDto;
import kdg.be.backend.domain.*;
import kdg.be.backend.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TurnService {
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final PlayingFieldService playingFieldService;
    private final MoveValidationService moveValidationService;
    private final DeckRepository deckRepository;
    private final TileRepository tileRepository;
    private final TilePoolRepository tilePoolRepository;
    private final GameUserAchievementService gameUserAchievementService;

    private static final Logger log = LoggerFactory.getLogger(TurnService.class);

    public TurnService(GameRepository gameRepository, PlayerRepository playerRepository, PlayingFieldService playingFieldService, MoveValidationService moveValidationService, DeckRepository deckRepository, TileRepository tileRepository, TilePoolRepository tilePoolRepository, GameUserAchievementService gameUserAchievementService) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.playingFieldService = playingFieldService;
        this.moveValidationService = moveValidationService;
        this.deckRepository = deckRepository;
        this.tileRepository = tileRepository;
        this.tilePoolRepository = tilePoolRepository;
        this.gameUserAchievementService = gameUserAchievementService;
    }

    private void managePlayerTurns(Player player, List<UUID> playerTurnOrders) {
        if (!player.getId().equals(playerTurnOrders.getFirst())) {
            throw new IllegalStateException(player.getGameUser().getUsername() + ": it's not your turn, wait until its your turn to make a move");
        }
    }

    @Transactional
    public Optional<Player> managePlayerMoves(UUID playerId, UUID gameId, List<PlayerMoveTileSetDto> tileSetDtos, List<PlayerMoveTileDto> deckDto) {
        return Optional.of(gameRepository.findGameById(gameId)
                .map(game -> {
                    Player player = playerRepository.findPlayerInGameByGameIdAndPlayerId(gameId, playerId)
                            .orElseThrow(() -> new NullPointerException("Player trying to play not found"));

                    List<UUID> playerTurnOrders = gameRepository.findPlayerTurnOrdersByGameId(gameId)
                            .orElseThrow(() -> new NullPointerException("Player turn orders not found"));

                    managePlayerTurns(player, playerTurnOrders);

                    if (LocalTime.now().isAfter(player.getTurnStartTime()) && LocalTime.now().isBefore(player.getTurnEndTime())) {
                        makePlayerMove(player, gameId, tileSetDtos, deckDto);
                    } else {
                        log.warn("{} didn't make a move when it was their turn from {} to {}. Move was made at {}"
                                , player.getGameUser().getUsername(), player.getTurnStartTime(), player.getTurnEndTime(), LocalTime.now());
                    }

                    log.info("Player with id {}, has score {}", player.getId(), player.getScore());
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
                    nextPlayer.setTurnStartTime(LocalTime.now());
                    nextPlayer.setTurnEndTime(nextPlayer.getTurnStartTime().plusSeconds(game.getTurnTime()));
                    playerRepository.save(nextPlayer);
                });

        log.info("The next turn is for player: {}, make a move within the time limit: from {} to {}",
                nextPlayer.getGameUser().getUsername(), nextPlayer.getTurnStartTime(), nextPlayer.getTurnEndTime());
        return nextPlayer;
    }

    private void makePlayerMove(Player player, UUID gameId, List<PlayerMoveTileSetDto> tileSetDtos, List<PlayerMoveTileDto> deckDto) {
        checkFirstTurn(player, deckDto);
        playingFieldService.handlePlayerMoves(gameId, tileSetDtos);
        playingFieldService.handlePlayerDeck(player.getId(), deckDto);
        log.info("Player {} made a move within the time limit: from {} to {}, move was made at {}",
                player.getGameUser().getUsername(), player.getTurnStartTime(), player.getTurnEndTime(),
                LocalTime.now());
        gameUserAchievementService.checkAndAssignFirstMoveAchievement(player.getGameUser().getId());
        gameUserAchievementService.checkAndAssignParticipationAchievement(player.getGameUser().getId());
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

        if (LocalTime.now().isAfter(player.getTurnStartTime()) && LocalTime.now().isBefore(player.getTurnEndTime())) {
            drawnTileFromTilePool = drawTileFromTilePool(player, playerTurnOrders, game);
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