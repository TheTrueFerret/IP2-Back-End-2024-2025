package kdg.be.backend.repository;

import kdg.be.backend.domain.GameUser;
import kdg.be.backend.domain.Lobby;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GameUserRepository extends JpaRepository<GameUser, UUID> {
}
