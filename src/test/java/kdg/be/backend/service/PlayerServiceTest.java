package kdg.be.backend.service;

import kdg.be.backend.TestContainerIPConfiguration;
import kdg.be.backend.domain.*;
import kdg.be.backend.domain.enums.GameState;
import kdg.be.backend.domain.enums.LobbyStatus;
import kdg.be.backend.domain.enums.TileColor;
import kdg.be.backend.domain.user.GameUser;
import kdg.be.backend.repository.GameRepository;
import kdg.be.backend.repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestContainerIPConfiguration.class)
public class PlayerServiceTest {
    @MockBean
    private PlayerRepository playerRepository;

    @MockBean
    private GameRepository gameRepository;

    @Autowired
    private PlayerService playerService;

    @Test
    @DirtiesContext
    void checkPlayerTiles_PlayerWins_ShouldEndGameAndCalculateScoresAndSetLeaderboard() {
        // Arrange
        GameUser gameUser = new GameUser("Quandale","test.png", new ArrayList<>(), null);
        GameUser gameUser2 = new GameUser("Max","test.png", new ArrayList<>(), null);

        List<Tile> tiles1 = new ArrayList<>();
        List<Tile> tiles2 = new ArrayList<>();

        tiles2.add(new Tile(1, TileColor.RED));
        tiles2.add(new Tile(5, TileColor.RED));
        tiles2.add(new Tile(10, TileColor.BLUE));

        Deck deck = new Deck(tiles1);
        Deck deck2 = new Deck(tiles2);

        PlayingField playingField = new PlayingField(new ArrayList<>());
        TilePool tilePool = new TilePool(new ArrayList<>());

        List<Player> players = new ArrayList<>();

        List<GameUser> gameUsers = new ArrayList<>();
        gameUsers.add(gameUser);
        gameUsers.add(gameUser2);

        Lobby lobby = new Lobby(LobbyStatus.READY, gameUser, gameUsers, "123TEST", 2, 4);
        Game game = new Game(60, 14, LocalDateTime.now(),playingField,tilePool, new ArrayList<>(),lobby);

        Player winner = new Player(gameUser,deck,game);
        Player player2 = new Player(gameUser2,deck2,game);

        players.add(winner);
        players.add(player2);
        game.setPlayers(players);

        // Act
        playerService.checkPlayerTiles(winner);

        // Assert
        assertEquals(game.getGameState(), GameState.ENDED);
        assertEquals(game.getPlayerLeaderboard().getFirst(), winner.getId());
        verify(gameRepository, atLeastOnce()).save(any(Game.class));
        verify(playerRepository, atLeastOnce()).save(any(Player.class));
    }

    @Test
    @DirtiesContext
    void checkPlayerTiles_PlayerWins_ShouldNotEndGame() {
        // Arrange
        GameUser gameUser = new GameUser("Quandale","test.png", new ArrayList<>(), null);
        GameUser gameUser2 = new GameUser("Max","test.png", new ArrayList<>(), null);

        List<Tile> tiles1 = new ArrayList<>();
        List<Tile> tiles2 = new ArrayList<>();

        tiles1.add(new Tile(11, TileColor.RED));
        tiles2.add(new Tile(1, TileColor.RED));
        tiles2.add(new Tile(5, TileColor.RED));
        tiles2.add(new Tile(10, TileColor.BLUE));

        Deck deck = new Deck(tiles1);
        Deck deck2 = new Deck(tiles2);

        PlayingField playingField = new PlayingField(new ArrayList<>());
        TilePool tilePool = new TilePool(new ArrayList<>());

        List<Player> players = new ArrayList<>();

        List<GameUser> gameUsers = new ArrayList<>();
        gameUsers.add(gameUser);
        gameUsers.add(gameUser2);

        Lobby lobby = new Lobby(LobbyStatus.READY, gameUser, gameUsers, "123TEST", 2, 4);
        Game game = new Game(60, 14, LocalDateTime.now(),playingField,tilePool, new ArrayList<>(),lobby);

        Player winner = new Player(gameUser,deck,game);
        Player player2 = new Player(gameUser2,deck2,game);

        players.add(winner);
        players.add(player2);
        game.setPlayers(players);

        // Act
        playerService.checkPlayerTiles(winner);

        // Assert
        assertEquals(game.getGameState(), GameState.ONGOING);
        verify(gameRepository, never()).save(any(Game.class));
        verify(playerRepository, never()).save(any(Player.class));
    }

    @Test
    @DirtiesContext
    void checkPlayerWhoWinsAfterCalculatingNoTilesInTilePool_ShouldSetGameLeaderboard() {
        // Arrange
        GameUser gameUser = new GameUser("Quandale","test.png", new ArrayList<>(), null);
        GameUser gameUser2 = new GameUser("Max","test.png", new ArrayList<>(), null);

        List<Tile> tiles1 = new ArrayList<>();
        List<Tile> tiles2 = new ArrayList<>();

        tiles1.add(new Tile(11, TileColor.RED));
        tiles1.add(new Tile(12, TileColor.RED));
        tiles1.add(new Tile(1, TileColor.RED));

        tiles2.add(new Tile(1, TileColor.RED));
        tiles2.add(new Tile(5, TileColor.RED));
        tiles2.add(new Tile(10, TileColor.BLUE));

        Deck deck = new Deck(tiles1);
        Deck deck2 = new Deck(tiles2);

        PlayingField playingField = new PlayingField(new ArrayList<>());
        TilePool tilePool = new TilePool(new ArrayList<>());

        List<Player> players = new ArrayList<>();

        List<GameUser> gameUsers = new ArrayList<>();
        gameUsers.add(gameUser);
        gameUsers.add(gameUser2);

        Lobby lobby = new Lobby(LobbyStatus.READY, gameUser, gameUsers, "123TEST", 2, 4);
        Game game = new Game(60, 14, LocalDateTime.now(),playingField,tilePool, new ArrayList<>(),lobby);

        Player winner = new Player(gameUser,deck,game);
        Player player2 = new Player(gameUser2,deck2,game);

        players.add(winner);
        players.add(player2);
        game.setPlayers(players);

        // Act
        playerService.calculateNoTilesInTilePoolPlayerScores(game);

        // Assert
        assertEquals(game.getGameState(), GameState.ENDED);
        assertEquals(game.getPlayerLeaderboard().getFirst(), player2.getId());
        verify(gameRepository, atLeastOnce()).save(any(Game.class));
        verify(playerRepository, atLeastOnce()).save(any(Player.class));
    }

    @Test
    @DirtiesContext
    void checkPlayerWhoWinsAfterCalculatingNoTilesInTilePool_ShouldNotSetGameLeaderboard() {
        // Arrange
        GameUser gameUser = new GameUser("Quandale","test.png", new ArrayList<>(), null);
        GameUser gameUser2 = new GameUser("Max","test.png", new ArrayList<>(), null);

        List<Tile> tiles1 = new ArrayList<>();
        List<Tile> tiles2 = new ArrayList<>();

        tiles1.add(new Tile(11, TileColor.RED));
        tiles1.add(new Tile(12, TileColor.RED));
        tiles1.add(new Tile(1, TileColor.RED));

        tiles2.add(new Tile(1, TileColor.RED));
        tiles2.add(new Tile(5, TileColor.RED));
        tiles2.add(new Tile(10, TileColor.BLUE));

        Deck deck = new Deck(tiles1);
        Deck deck2 = new Deck(tiles2);

        PlayingField playingField = new PlayingField(new ArrayList<>());

        List<Tile> tilePoolTiles = new ArrayList<>();
        tilePoolTiles.add(new Tile(1, TileColor.RED));
        tilePoolTiles.add(new Tile(1, TileColor.BLACK));
        tilePoolTiles.add(new Tile(1, TileColor.BLUE));
        TilePool tilePool = new TilePool(tilePoolTiles);

        List<Player> players = new ArrayList<>();

        List<GameUser> gameUsers = new ArrayList<>();
        gameUsers.add(gameUser);
        gameUsers.add(gameUser2);

        Lobby lobby = new Lobby(LobbyStatus.READY, gameUser, gameUsers, "123TEST", 2, 4);
        Game game = new Game(60, 14, LocalDateTime.now(),playingField,tilePool, new ArrayList<>(),lobby);

        Player winner = new Player(gameUser,deck,game);
        Player player2 = new Player(gameUser2,deck2,game);

        players.add(winner);
        players.add(player2);
        game.setPlayers(players);

        // Act
        playerService.calculateNoTilesInTilePoolPlayerScores(game);

        // Assert
        assertEquals(game.getGameState(), GameState.ONGOING);
        assertFalse(game.getTilePool().getTiles().isEmpty());
        verify(gameRepository, never()).save(any(Game.class));
        verify(playerRepository, never()).save(any(Player.class));
    }
}