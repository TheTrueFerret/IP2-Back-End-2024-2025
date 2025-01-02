package kdg.be.backend.exception;

public class FriendRequestException extends RuntimeException {
    public FriendRequestException(String message) {
        super("Friend request error : " + message);
    }
}
