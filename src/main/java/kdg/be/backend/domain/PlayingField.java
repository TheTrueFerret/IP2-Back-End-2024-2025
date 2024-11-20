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
    @OneToMany
    private Collection<TileSet> tileSets;

    public PlayingField() {
    }  // jpa

    public PlayingField(Collection<TileSet> tileSets) {
        this.tileSets = tileSets;
    }
}
