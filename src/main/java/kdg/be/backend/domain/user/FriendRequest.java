package kdg.be.backend.domain.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
public class FriendRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private GameUser sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private GameUser receiver;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    public FriendRequest() {}

    public FriendRequest(GameUser sender, GameUser receiver, RequestStatus status) {
        this.sender = sender;
        this.receiver = receiver;
        this.status = status;
    }

    // Getters and setters
}
