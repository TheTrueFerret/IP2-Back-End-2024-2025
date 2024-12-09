package kdg.be.backend.domain.chatting;

import jakarta.persistence.*;
import kdg.be.backend.domain.user.GameUser;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.UUID;

@Entity
@Getter
@Setter
public class ChatHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // relaties
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private GameUser gameUser;
    @OneToMany
    private Collection<ChatMessage> messages;

    public ChatHistory() {
    }  // jpa

    public ChatHistory(GameUser gameUser, Collection<ChatMessage> messages) {
        this.gameUser = gameUser;
        this.messages = messages;
    }
}
