package kdg.be.backend.repository;

import kdg.be.backend.domain.Game;
import kdg.be.backend.domain.TilePool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GameRepository extends JpaRepository<Game, UUID> {
    @Query("""
            SELECT g
            FROM Game g
            JOIN FETCH g.players p
            JOIN FETCH p.gameUser
            JOIN FETCH g.tilePool
            JOIN FETCH g.playingField
            WHERE g.id = :id
            """)
    Optional<Game> findGameById(UUID id);

    @Query("""
            SELECT g
            FROM Game g
            JOIN FETCH g.playingField pf
            LEFT JOIN FETCH pf.tileSets ts
            LEFT JOIN FETCH ts.tiles
            WHERE g.id = :gameId
            """)
    Optional<Game> findGameByIdWithPlayingField(UUID gameId);

    @Query("""
            SELECT g
            FROM Game g
            JOIN FETCH g.tilePool tp
            JOIN FETCH tp.tiles t
            WHERE g.id = :id AND t.deck IS NULL
            """)
    Optional<Game> findGameByIdWithTilePoolTilesWithDeckIsNull(UUID id);

    @Query("""
            SELECT g.playerTurnOrder
            FROM Game g
            WHERE g.id = :id
            """)
    Optional<List<UUID>> findPlayerTurnOrdersByGameId(UUID id);

    @Query("""
            SELECT count(g)
            FROM Game g
            WHERE g.lobby.id = :lobbyId
            """)
    int countGamesByLobbyId(UUID lobbyId);


    @Query("""
            SELECT g
            FROM Game g
            WHERE g.lobby.id = :lobbyId
            """)
    Optional<Game> findGameByLobbyId(UUID lobbyId);

    int countGamesByPlayersGameUserId(UUID gameUserId);


    @Query("""
            SELECT tp
            FROM Game g
            JOIN g.tilePool tp
            JOIN FETCH tp.tiles
            WHERE g.id = :gameId
            """)
    Optional<TilePool> findTilePoolByGameId(UUID gameId);


    @Query("""
            SELECT g
            FROM Game g
            JOIN FETCH g.players p
            WHERE p.id = :playerId
            """)
    Optional<Game> findGameByPlayerId(UUID playerId);
}