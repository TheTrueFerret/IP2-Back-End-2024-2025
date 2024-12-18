package kdg.be.backend.repository;

import kdg.be.backend.domain.TilePool;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TilePoolRepository extends JpaRepository<TilePool, UUID> {
}
