package kdg.be.backend.repository;

import kdg.be.backend.domain.Game;
import kdg.be.backend.domain.GameUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GameRepository extends JpaRepository<Game, UUID> {
}
