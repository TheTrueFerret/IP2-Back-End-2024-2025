package kdg.be.backend.repository;

import kdg.be.backend.domain.Game;
import kdg.be.backend.domain.PlayingField;
import kdg.be.backend.domain.TileSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlayingFieldRepository extends JpaRepository<PlayingField, UUID> {
    @Query("SELECT pf FROM PlayingField pf LEFT JOIN FETCH pf.tileSets WHERE pf.id = :id")
    Optional<PlayingField> findByIdWithTileSets(UUID id);


}
