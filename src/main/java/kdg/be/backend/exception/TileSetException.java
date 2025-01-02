package kdg.be.backend.exception;

public class TileSetException extends RuntimeException {
    public TileSetException(String message) {
        super("TileSetException: " + message);
    }
}
