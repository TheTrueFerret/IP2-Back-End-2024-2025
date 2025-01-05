package kdg.be.backend.controller.dto.prediction;

import kdg.be.backend.domain.ai.Prediction;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class PredictionDto {
    private String gameName;
    private LocalDateTime prediction_date;
    private double rating_average;
    private double complexity_average;
    private int owned_users;

    public PredictionDto(Prediction prediction) {
        this.gameName = prediction.getGameStat().getGameName();
        this.prediction_date = prediction.getPrediction_date();
        this.rating_average = prediction.getRating_average();
        this.complexity_average = prediction.getComplexity_average();
        this.owned_users = prediction.getOwned_users();
    }

    public static List<PredictionDto> toDtoList(List<Prediction> predictions) {
        List<PredictionDto> predictionDtos = new ArrayList<>();
        for (Prediction prediction : predictions) {
            predictionDtos.add(new PredictionDto(prediction));
        }
        return predictionDtos;
    }

}