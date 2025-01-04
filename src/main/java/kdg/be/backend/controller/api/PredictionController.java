package kdg.be.backend.controller.api;

import kdg.be.backend.domain.ai.Prediction;
import kdg.be.backend.service.PredictionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class PredictionController {

    private final PredictionService predictionService;

    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @GetMapping("/prediction/{GameName}")
    public ResponseEntity<Prediction> getPrediction(@PathVariable String GameName) {
        if (GameName == null || GameName.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(predictionService.getPrediction(GameName));
    }
}
