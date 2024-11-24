package kdg.be.backend.domain;

import jakarta.persistence.*;
import kdg.be.backend.domain.enums.LobbyStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Lobby {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Enumerated(EnumType.STRING)
    private LobbyStatus status;
    private String joinCode;
    private int minimumPlayers;
    private int maximumPlayers;

    // relaties
    @OneToOne(fetch = FetchType.LAZY)
    private GameUser hostUser;
    @OneToMany
    private List<GameUser> users;

    public Lobby() {
    } // jpa

    public Lobby(LobbyStatus status, GameUser hostUser, List<GameUser> users, String joinCode, int minimumPlayers, int maximumPlayers) {
        this.status = status;
        this.hostUser = hostUser;
        this.users = users;
        this.joinCode = joinCode;
        this.minimumPlayers = minimumPlayers;
        this.maximumPlayers = maximumPlayers;
    }
}
