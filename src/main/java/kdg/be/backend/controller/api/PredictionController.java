package kdg.be.backend.controller.api;

import kdg.be.backend.controller.dto.prediction.FormDataDto;
import kdg.be.backend.controller.dto.prediction.PredictionDto;
import kdg.be.backend.exception.PredictionException;
import kdg.be.backend.service.PredictionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/predictions")
public class PredictionController {

    private final PredictionService predictionService;

    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @GetMapping("/{GameName}")
    public ResponseEntity<List<PredictionDto>> getAllPredictions(@PathVariable String GameName) {
        if (GameName == null || GameName.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        List<PredictionDto> predictions = predictionService.getAllPredictions(GameName);
        return ResponseEntity.ok(predictions);
    }

    @PostMapping("/{GameName}")
    public ResponseEntity<?> createPrediction(@PathVariable String GameName,@RequestBody FormDataDto formDataDto) {
        if (GameName == null || GameName.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        predictionService.createPrediction(GameName,formDataDto);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(PredictionException.class)
    public ResponseEntity<?> handlePredictionException(PredictionException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", NullPointerException.class.getSimpleName());
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}
