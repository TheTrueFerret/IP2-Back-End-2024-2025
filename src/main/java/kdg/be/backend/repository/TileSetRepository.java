package kdg.be.backend.repository;

import kdg.be.backend.domain.PlayingField;
import kdg.be.backend.domain.TilePosition;
import kdg.be.backend.domain.TileSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TileSetRepository extends JpaRepository<TileSet, UUID> {
    @Query("SELECT ts FROM TileSet ts LEFT JOIN FETCH ts.tiles WHERE ts.id = :id")
    Optional<TileSet> findByIdWithTiles(UUID id);

    @Query("SELECT DISTINCT ts FROM PlayingField pf JOIN pf.tileSets ts JOIN FETCH ts.tiles WHERE pf.id = :playingFieldId")
    List<TileSet> findAllByPlayingFieldIdWithTiles(@Param("playingFieldId") UUID playingFieldId);
}
