package kdg.be.backend.repository;

import kdg.be.backend.domain.Tile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TileRepository extends JpaRepository<Tile, UUID> {
    @Query("SELECT t FROM Tile t WHERE t.id IN (" +
            "SELECT dt.id FROM Deck d " +
            "JOIN d.tiles dt " +
            "JOIN Player p ON p.deck.id = d.id " +
            "WHERE p.id = :playerId)")
    Optional<List<Tile>> findDeckTilesByPlayerId(@Param("playerId") UUID playerId);

    @Query("SELECT t FROM Tile t WHERE t.tilePool.id = :tilePoolId")
    List<Tile> findTilesByTilePoolId(@Param("tilePoolId") UUID tilePoolId);

    @Query("SELECT t FROM Tile t WHERE t.tileSet.id = :tileSetId")
    List<Tile> findTilesByTileSetId(@Param("tileSetId") UUID tileSetId);

    @Query("SELECT t FROM Tile t WHERE t.deck.id = :deckId")
    List<Tile> findTilesByDeckId(@Param("deckId") UUID deckId);

    @Query("SELECT t FROM Tile t LEFT JOIN FETCH t.tileSet ts LEFT JOIN FETCH ts.tiles WHERE t.id IN :tileIds")
    List<Tile> findAllByIdWithTileSetAndTiles(@Param("tileIds") List<UUID> tileIds);

    @Query("SELECT t FROM Tile t JOIN FETCH t.tileSet ts JOIN FETCH ts.playingField WHERE t.id IN :tileIds")
    List<Tile> findTilesWithRelationsByIds(@Param("tileIds") List<UUID> tileIds);
}
