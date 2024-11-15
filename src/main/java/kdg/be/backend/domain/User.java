package kdg.be.backend.domain;

////import jakarta.persistence.*;
import kdg.be.backend.domain.chatting.ChatHistory;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

//@Entity
@Getter
@Setter
public class User {
//    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String username;
    private String avatar;

    // relaties
////    @OneToMany
    private List<Achievement> achievements;
////    @OneToMany
    private List<User> friendList;
////    @ManyToOne(fetch = FetchType.LAZY)
    private ChatHistory chatHistory;

    public User() {
    } // jpa

    public User(String username, String avatar, List<Achievement> achievements, List<User> friendList, ChatHistory chatHistory) {
        this.username = username;
        this.avatar = avatar;
        this.achievements = achievements;
        this.friendList = friendList;
        this.chatHistory = chatHistory;
    }
}
