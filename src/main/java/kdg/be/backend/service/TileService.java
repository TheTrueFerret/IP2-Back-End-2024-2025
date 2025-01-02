package kdg.be.backend.service;

import kdg.be.backend.domain.Player;
import kdg.be.backend.domain.Tile;
import kdg.be.backend.domain.enums.TileColor;
import kdg.be.backend.repository.DeckRepository;
import kdg.be.backend.repository.TilePoolRepository;
import kdg.be.backend.repository.TileRepository;
import kdg.be.backend.repository.TileSetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Service
public class TileService {
    private final TileRepository tileRepository;
    private final TilePoolRepository tilePoolRepository;
    private final TileSetRepository tileSetRepository;
    private final DeckRepository deckRepository;

    private static final Logger log = LoggerFactory.getLogger(TileService.class);

    public TileService(TileRepository tileRepository, TilePoolRepository tilePoolRepository, TileSetRepository tileSetRepository, DeckRepository deckRepository) {
        this.tileRepository = tileRepository;
        this.tilePoolRepository = tilePoolRepository;
        this.tileSetRepository = tileSetRepository;
        this.deckRepository = deckRepository;
    }

    public List<Tile> getAllTiles() {
        return tileRepository.findAll();
    }

    public Tile getTileById(UUID id) {
        return tileRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Tile not found"));
    }

    public List<Tile> getTilesByTilePoolId(UUID tilePoolId) {
        if (!tilePoolRepository.existsById(tilePoolId)) {
            throw new IllegalArgumentException("TilePool not found for ID: " + tilePoolId);
        }
        return tileRepository.findTilesByTilePoolId(tilePoolId);
    }

    public List<Tile> getTilesByTileSetId(UUID tileSetId) {
        if (!tileSetRepository.existsById(tileSetId)) {
            throw new IllegalArgumentException("TileSet not found for ID: " + tileSetId);
        }
        return tileRepository.findTilesByTileSetId(tileSetId);
    }

    public List<Tile> getTilesByDeckId(UUID deckId) {
        if (!deckRepository.existsById(deckId)) {
            throw new IllegalArgumentException("Deck not found for ID: " + deckId);
        }
        return tileRepository.findTilesByDeckId(deckId);
    }

    public List<Tile> createTiles(int amount) {
        List<Tile> tiles = new ArrayList<>();
        TileColor[] colors = TileColor.values();

        for (int number = 1; number <= 13; number++) {
            for (TileColor color : colors) {
                tiles.add(new Tile(number, color));
                tiles.add(new Tile(number, color)); // Dubbele set per kleur en nummer
            }
        }

        tiles.add(new Tile(0, TileColor.BLACK)); // Joker 1
        tiles.add(new Tile(0, TileColor.RED)); // Joker 2

        log.info("AMOUNT OF TILES GENERATED: {}", tiles.size());

        if (tiles.size() < amount) {
            throw new IllegalArgumentException("Not enough tiles to create a pool");
        }

        return tiles;
    }

    public void validateEqualTileCounts(List<Player> players, int startTileAmount) {
        for (Player player : players) {
            int playerTileCount = player.getDeck().getTiles().size();
            if (playerTileCount != startTileAmount) {
                throw new IllegalStateException("Tile distribution error: Players do not have equal tile counts.");
            }
        }

        log.info("All players have equal tile counts: {}", startTileAmount);
    }

    public void checkTileSet(LinkedList<Tile> tileGroup) {
        if (tileGroup.size() < 3) {
            throw new IllegalStateException("The tile set must contain at least 3 tiles.");
        }

        for (int i = 0; i < tileGroup.size(); i++) {
            // Skip the first tile
            if (i > 0) {
                Tile currentTile = tileGroup.get(i);
                Tile previousTile = tileGroup.get(i - 1);

                //If last tile is a joker, skip check
                if (i == tileGroup.size() - 1 && currentTile.getNumberValue() == 0 || previousTile.getNumberValue() == 0) {
                    continue;
                }

                if (currentTile.getNumberValue() == 0) {
                    // Check if joker is last tile
                    if (i < tileGroup.size() - 1) {
                        Tile nextTile = tileGroup.get(i + 1);

                        // Jokers bridging a sequence for the same color tiles
                        if (nextTile.getTileColor().equals(previousTile.getTileColor())) {
                            if (nextTile.getNumberValue() != previousTile.getNumberValue() + 2) {
                                throw new IllegalStateException("Joker does not correctly bridge a sequence for tiles of the same color.");
                            }
                        }
                        // Jokers matching numbers between different colors tiles
                        else {
                            if (nextTile.getNumberValue() != previousTile.getNumberValue()) {
                                throw new IllegalStateException("Joker does not correctly match numbers between different colors.");
                            }
                        }
                    }
                    continue;
                }


                if (currentTile.getTileColor().equals(previousTile.getTileColor())) {
                    if (currentTile.getNumberValue() != previousTile.getNumberValue() + 1) {
                        throw new IllegalStateException("Tiles of the same color must be in sequential order.");
                    }
                } else {
                    if (currentTile.getNumberValue() != previousTile.getNumberValue()) {
                        throw new IllegalStateException("Tiles of different colors must have the same number.");
                    }
                }
            }
        }
    }
}
