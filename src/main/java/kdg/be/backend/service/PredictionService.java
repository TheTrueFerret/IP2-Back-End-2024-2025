package kdg.be.backend.service;

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


    public PredictionDto getLastPrediction(String gameName) {
        Prediction prediction = predictionRepository.getLastPredictionByGameStatName(gameName);
        if (prediction == null) {
            if (createPrediction(gameName)) {
                prediction = predictionRepository.getLastPredictionByGameStatName(gameName);
            } else {
                throw new PredictionException("Prediction not found");
            }
        }
        return new PredictionDto(prediction);
    }

    public boolean createPrediction(String gameName) {
        GameStat gameStat = gameStatRepository.findByGameNameIsIgnoreCase(gameName);
        if (gameStat == null) {
            throw new PredictionException("GameStat not found");
        }
        GameStatDto body = new GameStatDto(gameStat.getYear_published(), gameStat.getMin_players(), gameStat.getMax_players(), gameStat.getPlay_time(), gameStat.getMin_age(), gameStat.getBoard_game_honor(), gameStat.getMechanics());
        Prediction prediction = restTemplate.postForObject(predictionApiBaseUrl, body, Prediction.class);
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
