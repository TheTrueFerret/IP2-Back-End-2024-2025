package kdg.be.backend.service;

import kdg.be.backend.controller.dto.prediction.FormDataDto;
import kdg.be.backend.controller.dto.prediction.GameStatDto;
import kdg.be.backend.controller.dto.prediction.PredictionDto;
import kdg.be.backend.domain.ai.GameStat;
import kdg.be.backend.domain.ai.Prediction;
import kdg.be.backend.exception.PredictionException;
import kdg.be.backend.repository.GameStatRepository;
import kdg.be.backend.repository.PredictionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PredictionService {

    private final RestTemplate restTemplate;
    private final PredictionRepository predictionRepository;
    private final GameStatRepository gameStatRepository;

    @Value("${prediction.api.base-url}")
    private String predictionApiBaseUrl;

    public PredictionService(RestTemplate restTemplate, PredictionRepository predictionRepository, GameStatRepository gameStatRepository) {
        this.restTemplate = restTemplate;
        this.predictionRepository = predictionRepository;
        this.gameStatRepository = gameStatRepository;
    }

    public boolean createPrediction(String gameName, FormDataDto formDataDto) {
        GameStat gameStat = gameStatRepository.findByGameNameIsIgnoreCase(gameName);
        if (gameStat == null) {
            throw new PredictionException("GameStat not found");
        }
        GameStatDto gameStatDto = new GameStatDto(
                gameStat.getYear_published(),
                formDataDto.getMin_players() != 0 ? formDataDto.getMin_players() : gameStat.getMin_players(),
                formDataDto.getMax_players() != 0 ? formDataDto.getMax_players() : gameStat.getMax_players(),
                formDataDto.getPlay_time() != 0 ? formDataDto.getPlay_time() : gameStat.getPlay_time(),
                gameStat.getMin_age(),
                formDataDto.getBoard_game_honor() != 0 ? formDataDto.getBoard_game_honor() : gameStat.getBoard_game_honor(),
                formDataDto.getMechanics() != null && !formDataDto.getMechanics().isEmpty() ? formDataDto.getMechanics() : gameStat.getMechanics()
        );
        Prediction prediction = restTemplate.postForObject(predictionApiBaseUrl, gameStatDto, Prediction.class);
        if (prediction == null) {
            throw new PredictionException("Prediction not created");
        }
        prediction.setId(UUID.randomUUID());
        prediction.setGameStat(gameStat);
        prediction.setPrediction_date(LocalDateTime.now());
        predictionRepository.save(prediction);
        return true;
    }

    public List<PredictionDto> getAllPredictions(String gameName) {
        if (predictionRepository.getAllPredictionsByGameStatIgnoreCase_GameName(gameName).isEmpty()) {
            throw new PredictionException("Predictions not found");
        }
        return PredictionDto.toDtoList(predictionRepository.getAllPredictionsByGameStatIgnoreCase_GameName(gameName));
    }
}
