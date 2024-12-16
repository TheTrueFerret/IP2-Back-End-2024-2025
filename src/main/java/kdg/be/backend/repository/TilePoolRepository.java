package kdg.be.backend.repository;

import kdg.be.backend.domain.Game;
import kdg.be.backend.domain.TilePool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface TilePoolRepository extends JpaRepository<TilePool, UUID> {
}
