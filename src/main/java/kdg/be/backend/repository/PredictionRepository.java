package kdg.be.backend.repository;

import kdg.be.backend.domain.ai.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PredictionRepository extends JpaRepository<Prediction, UUID> {
}
