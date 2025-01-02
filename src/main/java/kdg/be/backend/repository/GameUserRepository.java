package kdg.be.backend.repository;


import kdg.be.backend.domain.user.GameUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GameUserRepository extends JpaRepository<GameUser, UUID> {

    @Query("SELECT g FROM GameUser g " +
            "LEFT JOIN FETCH g.friendList " +
            "WHERE g.id = :id")
    Optional<kdg.be.backend.domain.user.GameUser> findGameUserWithDetails(@Param("id") UUID id);

    @Query("SELECT g FROM GameUser g " +
            "LEFT JOIN FETCH g.friendList " +
            "WHERE g.username = :username")
    Optional<GameUser> findGameUserByUsername(String username);

    boolean existsByUsername(String username);

    @Query("SELECT g FROM GameUser g " +
            "LEFT JOIN FETCH g.achievements")
    List<GameUser> findAllWithAchievements();

    @Query("SELECT g FROM GameUser g " +
            "LEFT JOIN FETCH g.friendList " +
            "WHERE LOWER(g.username) LIKE LOWER(CONCAT('%', :username, '%'))")
    List<GameUser> findGameUsersByUsernameIsContainingIgnoreCase(@Param("username") String username);

    @Query("SELECT COUNT(g) FROM Game g JOIN g.players p WHERE p.gameUser.id = :userId")
    long countGamesPlayedByUser(UUID userId);
}
