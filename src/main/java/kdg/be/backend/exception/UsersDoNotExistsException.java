package kdg.be.backend.exception;

public class UsersDoNotExistsException extends RuntimeException {
    public UsersDoNotExistsException(String message) {
        super(message);
    }
}
