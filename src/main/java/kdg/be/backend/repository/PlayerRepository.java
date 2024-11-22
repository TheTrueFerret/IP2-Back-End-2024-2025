package kdg.be.backend.repository;

import kdg.be.backend.domain.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PlayerRepository extends JpaRepository<Player, UUID> {
    @Query("SELECT p FROM Player p WHERE p.game.id = :gameId")
    List<Player> findPlayersByGameId(@Param("gameId") UUID gameId);
}
