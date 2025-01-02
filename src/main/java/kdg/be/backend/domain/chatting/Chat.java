package kdg.be.backend.domain.chatting;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import kdg.be.backend.domain.user.GameUser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Chat {
    @Id
    private UUID id;
    @ManyToOne
    private GameUser gameUser;

    public Chat() {
    }

    public Chat(UUID id, GameUser gameUser) {
        this.id = id;
        this.gameUser = gameUser;
    }
}
