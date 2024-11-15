package kdg.be.backend.domain.chatting;

//import jakarta.persistence.*;
import kdg.be.backend.domain.User;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.message.Message;

import java.util.Collection;
import java.util.UUID;

//@Entity
@Getter
@Setter
public class ChatHistory {
    //@Id
//    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // relaties
//    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
//    @OneToMany
    private Collection<ChatMessage> messages;

    public ChatHistory() {
    } // jpa

    public ChatHistory(User user, Collection<ChatMessage> messages) {
        this.user = user;
        this.messages = messages;
    }
}
