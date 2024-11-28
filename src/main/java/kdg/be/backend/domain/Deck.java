package kdg.be.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Deck {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

     // relaties
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tile> tiles;

    public Deck() {
    }  // jpa

    public Deck(List<Tile> tiles) {
        this.tiles = tiles;
    }
}
