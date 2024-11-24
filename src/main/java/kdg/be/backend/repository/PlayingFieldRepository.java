package kdg.be.backend.repository;

import kdg.be.backend.domain.PlayingField;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlayingFieldRepository extends JpaRepository<PlayingField, UUID> {
}
