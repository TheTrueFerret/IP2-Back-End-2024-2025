package kdg.be.backend.domain.chatting;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private LocalDateTime dateTime;
    private String message;

    public ChatMessage() {
    }  // jpa

    public ChatMessage(LocalDateTime dateTime, String message) {
        this.dateTime = dateTime;
        this.message = message;
    }
}