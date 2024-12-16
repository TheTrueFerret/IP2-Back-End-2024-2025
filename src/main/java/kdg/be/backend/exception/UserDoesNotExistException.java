package kdg.be.backend.exception;

public class UserDoesNotExistException extends RuntimeException {
    public UserDoesNotExistException(String uuid) {
        super("User with id " + uuid + " does not exist.");
    }
}
