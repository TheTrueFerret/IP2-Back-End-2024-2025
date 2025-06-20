package kdg.be.backend.repository;

import kdg.be.backend.domain.Deck;
import kdg.be.backend.domain.Player;
import kdg.be.backend.domain.enums.GameState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlayerRepository extends JpaRepository<Player, UUID> {
    @Query("""
            SELECT p
            FROM Player p
            JOIN FETCH p.gameUser
            WHERE p.game.id = :gameId
            """)
    Optional<List<Player>> findPlayersByGameId(@Param("gameId") UUID gameId);

    @Query("""
            SELECT p
            FROM Player p
            JOIN FETCH p.gameUser
            JOIN FETCH p.game g
            WHERE p.id = :playerId AND g.id = :gameId
            """)
    Optional<Player> findPlayerInGameByGameIdAndPlayerId(UUID gameId, UUID playerId);

    @Query("""
            SELECT p
            FROM Player p
            JOIN FETCH p.gameUser
            JOIN FETCH p.game
            JOIN FETCH p.deck d
            JOIN FETCH d.tiles
            WHERE p.id = :id
            """)
    Optional<Player> findPlayerById(UUID id);

    @Query("""
            SELECT p
            FROM Player p
            JOIN FETCH p.gameUser gu
            JOIN FETCH p.game g
            WHERE gu.id = :userId AND g.gameState = :gameState
            """)
    Optional<Player> findPlayerByUserId(UUID userId, GameState gameState);


}