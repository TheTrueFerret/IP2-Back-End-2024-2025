package kdg.be.backend.repository;

import kdg.be.backend.domain.user.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, UUID> {

    @Query("SELECT f FROM FriendRequest f " +
            "JOIN f.sender s " +
            "JOIN f.receiver r " +
            "WHERE s.id = :senderId AND r.id = :receiverId")
    FriendRequest findFriendRequestBySenderAndReceiver(UUID senderId, UUID receiverId);
}
