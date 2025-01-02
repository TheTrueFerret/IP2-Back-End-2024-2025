package kdg.be.backend.controller.dto.user;

import kdg.be.backend.domain.user.GameUser;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UserFriendDto {
    private UUID id;
    private String username;
    private String avatar;
    private boolean isFriend;

    public UserFriendDto(GameUser gameUser, boolean isFriend) {
        this.id = gameUser.getId();
        this.username = gameUser.getUsername();
        this.avatar = gameUser.getAvatar();
        this.isFriend = isFriend;
    }
}
