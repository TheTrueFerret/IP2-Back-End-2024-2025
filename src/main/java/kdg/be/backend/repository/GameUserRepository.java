package kdg.be.backend.repository;


import kdg.be.backend.domain.GameUser;
import kdg.be.backend.domain.user.GameUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GameUserRepository extends JpaRepository<GameUser, UUID> {

    @Query("SELECT g FROM GameUser g " +
            "LEFT JOIN FETCH g.friendList " +
            "WHERE g.id = :id")
    Optional<GameUser> findGameUserWithDetails(@Param("id") UUID id);

    @Query("SELECT g FROM GameUser g " +
            "LEFT JOIN FETCH g.friendList " +
            "WHERE g.username = :username")
    GameUser findGameUserByUsername(String username);
    boolean existsByUsername(String username);
}
