package kdg.be.backend.repository;

import kdg.be.backend.domain.ai.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface PredictionRepository extends JpaRepository<Prediction, UUID> {
    @Query("SELECT p FROM Prediction p WHERE p.gameStat.gameName = :gameName ORDER BY p.prediction_date DESC")
    Prediction getLastPredictionByGameStatName(String gameName);

    List<Prediction> getAllPredictionsByGameStatIgnoreCase_GameName(String gameName);
}
