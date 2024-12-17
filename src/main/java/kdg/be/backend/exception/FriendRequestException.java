package kdg.be.backend.exception;

public class FriendRequestException extends RuntimeException {
    public FriendRequestException(String message) {
        super("Error creating friend request: " + message);
    }
}
