package kdg.be.backend.repository;

import kdg.be.backend.domain.Lobby;
import kdg.be.backend.domain.enums.LobbyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LobbyRepository extends JpaRepository<Lobby, UUID> {
    @Query("""
            SELECT lo
            FROM Lobby lo
            JOIN FETCH lo.hostUser
            JOIN FETCH lo.users
            """)
    List<Optional<Lobby>> findAllLobbies();

    @Query("""
            SELECT lo
            FROM Lobby lo
            JOIN FETCH lo.hostUser
            JOIN FETCH lo.users
            WHERE lo.id = :id
            """)
    Optional<Lobby> findLobbyById(UUID id);

    @Query("""
            SELECT lo
            FROM Lobby lo
            JOIN FETCH lo.hostUser
            JOIN FETCH lo.users
            WHERE lo.joinCode = :joinCode
            """)
    Optional<Lobby> findLobbyByJoinCode(String joinCode);

    @Query("""
            SELECT lo
            FROM Lobby lo
            JOIN FETCH lo.users u
            JOIN FETCH lo.hostUser hu
            WHERE u.id = :userId OR hu.id = :userId
            """)
    Optional<Lobby> findLobbyByHostUserOrGameUserId(UUID userId);

    @Query("SELECT l FROM Lobby l WHERE l.status = :status")
    List<Lobby> findLobbiesByStatus(LobbyStatus status);
}
