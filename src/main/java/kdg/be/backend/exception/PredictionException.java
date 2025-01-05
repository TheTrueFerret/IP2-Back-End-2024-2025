package kdg.be.backend.exception;

public class PredictionException extends RuntimeException {
    public PredictionException(String message) {
        super("PredictionException: " + message);
    }
}
