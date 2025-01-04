package kdg.be.backend.repository;


import kdg.be.backend.domain.ai.GameStat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GameStatRepository extends JpaRepository<GameStat, UUID> {
    GameStat findByGameNameIsIgnoreCase(String gameName);
}
