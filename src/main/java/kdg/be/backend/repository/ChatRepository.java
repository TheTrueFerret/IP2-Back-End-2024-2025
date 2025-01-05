package kdg.be.backend.repository;

import kdg.be.backend.domain.chatting.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;
public interface ChatRepository extends JpaRepository<Chat, UUID> {
    List<Chat> findAllByGameUserId(UUID gameUserId);
}
