package kdg.be.backend.controller.dto.user;

import lombok.Getter;

@Getter
public class FriendRequestDto {
    private String requestId;
    private String senderName;
    private String senderId;

    public FriendRequestDto(String requestId,String senderName, String senderId) {
        this.requestId = requestId;
        this.senderName = senderName;
        this.senderId = senderId;
    }

}
