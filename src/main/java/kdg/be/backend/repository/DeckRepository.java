package kdg.be.backend.repository;

import kdg.be.backend.domain.Deck;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DeckRepository extends JpaRepository<Deck, UUID> {
}
