package kdg.be.backend.repository;

import kdg.be.backend.domain.Tile;
import kdg.be.backend.domain.TilePosition;
import kdg.be.backend.domain.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface TilePositionRepository extends JpaRepository<TilePosition, UUID> {
    @Query("SELECT tp FROM TilePosition tp JOIN FETCH tp.tile WHERE tp.game = :game")
    List<TilePosition> findByGameWithTile(Game game);

    @Query("SELECT tp FROM TilePosition tp JOIN FETCH tp.tile JOIN FETCH tp.game WHERE tp.game = :game AND tp.tile = :tile")
    TilePosition findByAndWithGameAndTile(Game game, Tile tile);
}
