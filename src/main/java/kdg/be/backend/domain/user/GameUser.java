package kdg.be.backend.domain.user;

import jakarta.persistence.*;
import kdg.be.backend.domain.chatting.Chat;
import kdg.be.backend.domain.customization.Customizable;
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
    private int points;

    // relaties
    @OneToMany(mappedBy = "gameUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameUserAchievement> achievements = new ArrayList<>();

    @ManyToMany
    private List<GameUser> friendList;

    @OneToMany(mappedBy = "gameUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chat> chats;

    @OneToMany(mappedBy = "gameUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Customizable> customizables;

    public GameUser() {
    }  // jpa

    public GameUser(String username, String avatar, List<GameUser> friendList, List<Chat> chats) {
        this.username = username;
        this.avatar = avatar;
        this.friendList = friendList;
        this.chats = chats;
    }

    public GameUser(UUID id, String username) {
        this.id = id;
        this.username = username;
        this.avatar = "default.png";
        this.achievements = new ArrayList<>();
        this.friendList = new ArrayList<>();
        this.chats = new ArrayList<>();
        this.customizables = new ArrayList<>();
        this.points = 0;
    }

    public int getAchievementLevel() {
        return this.achievements.size();
    }
}
