package kdg.be.backend.repository;

import kdg.be.backend.domain.chatting.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
public interface ChatRepository extends JpaRepository<Chat, UUID> {
}
