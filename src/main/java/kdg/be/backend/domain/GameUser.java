package kdg.be.backend.domain;

import jakarta.persistence.*;
import kdg.be.backend.domain.chatting.ChatHistory;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
public class GameUser {
    @Id
    private UUID id;
    private String username;
    private String avatar;

    // relaties
    @OneToMany(mappedBy = "gameUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameUserAchievement> achievements = new ArrayList<>();

    @OneToMany
    private List<GameUser> friendList;
    @OneToOne(mappedBy = "gameUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private ChatHistory chatHistory;

    public GameUser() {
    }  // jpa

    public GameUser(String username, String avatar, List<GameUser> friendList, ChatHistory chatHistory) {
        this.username = username;
        this.avatar = avatar;
        this.friendList = friendList;
        this.chatHistory = chatHistory;
    }

    public GameUser(UUID  id, String username) {
        this.id = id;
        this.username = username;
        this.avatar = "default.png";
        this.achievements = new ArrayList<>();
        this.friendList = new ArrayList<>();
        this.chatHistory = new ChatHistory(this, new ArrayList<>());
    }
}
