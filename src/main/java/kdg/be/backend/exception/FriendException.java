package kdg.be.backend.exception;

public class FriendException extends RuntimeException {
    public FriendException(String message) {
        super("Friend error : " + message);
    }
}
