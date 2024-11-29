package kdg.be.backend.repository;


import jakarta.transaction.Transactional;
import kdg.be.backend.domain.GameUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GameUserRepository extends JpaRepository<GameUser, UUID> {

    @Query("SELECT g FROM GameUser g " +
            "LEFT JOIN FETCH g.friendList " +
            "WHERE g.id = :id")
    GameUser findGameUserWithDetails(@Param("id") UUID id);

}
