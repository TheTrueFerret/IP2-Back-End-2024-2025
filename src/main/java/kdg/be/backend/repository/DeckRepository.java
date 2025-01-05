package kdg.be.backend.repository;

import kdg.be.backend.domain.Deck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeckRepository extends JpaRepository<Deck, UUID> {
    @Query("""
            SELECT p.deck
            FROM Player p
            WHERE p.game.id = :gameId
        """)
    List<Deck> findDecksByGameId(UUID gameId);
}
