package kdg.be.backend.repository;

import kdg.be.backend.domain.Game;
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
}