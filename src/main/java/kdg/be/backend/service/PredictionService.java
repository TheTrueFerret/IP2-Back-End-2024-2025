package kdg.be.backend.service;

import kdg.be.backend.controller.dto.prediction.GameStatDto;
import kdg.be.backend.domain.ai.GameStat;
import kdg.be.backend.domain.ai.Prediction;
import kdg.be.backend.repository.GameStatRepository;
import kdg.be.backend.repository.PredictionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PredictionService {

    RestTemplate restTemplate;
    PredictionRepository predictionRepository;
    GameStatRepository gameStatRepository;

    @Value("${prediction.api.base-url}")
    private String predictionApiBaseUrl;

    public PredictionService(RestTemplate restTemplate, PredictionRepository predictionRepository) {
        this.restTemplate = restTemplate;
        this.predictionRepository = predictionRepository;
    }


    public Prediction getPrediction(String gameName) {
        GameStat gameStat = gameStatRepository.findByGameNameIsIgnoreCase(gameName);
        GameStatDto body = new GameStatDto(gameStat.getYear_published(), gameStat.getMin_players(), gameStat.getMax_players(), gameStat.getPlay_time(), gameStat.getMin_age(), gameStat.getBoard_game_honor(), gameStat.getMechanics());
        Prediction prediction = restTemplate.postForObject(predictionApiBaseUrl,body,Prediction.class);
        if (prediction == null) {
            throw new IllegalArgumentException("Prediction is null");
        }
        predictionRepository.save(prediction);
        return prediction;
    }
}
