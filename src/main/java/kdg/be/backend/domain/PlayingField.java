package kdg.be.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.UUID;

@Entity
@Getter
@Setter
public class PlayingField {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

     // relaties
    @OneToMany(mappedBy = "playingField")
    private Collection<TileSet> tileSets;

    @OneToOne(mappedBy = "playingField", cascade = CascadeType.ALL, orphanRemoval = true)
    private Game game;

    public PlayingField() {
    }  // jpa

    public PlayingField(Collection<TileSet> tileSets) {
        this.tileSets = tileSets;
    }
}
