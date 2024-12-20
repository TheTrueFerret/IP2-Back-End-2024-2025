package kdg.be.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserDoesNotExistException extends RuntimeException {
    public UserDoesNotExistException(String uuid) {
        super("User with id " + uuid + " does not exist.");
    }
}
