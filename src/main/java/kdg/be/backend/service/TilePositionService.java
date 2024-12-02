package kdg.be.backend.service;

import kdg.be.backend.domain.Tile;
import kdg.be.backend.domain.TilePosition;
import kdg.be.backend.domain.Game;
import kdg.be.backend.repository.TilePositionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TilePositionService {
    private final TilePositionRepository tilePositionRepository;
    private static final Logger log = LoggerFactory.getLogger(GameService.class);


    public TilePositionService(TilePositionRepository tilePositionRepository) {
        this.tilePositionRepository = tilePositionRepository;
    }

    public List<TilePosition> getTilePositionsForGame(Game game) {
        return tilePositionRepository.findByGameWithTile(game);
    }

    @Transactional
    public TilePosition assignTileToPosition(Game game, Tile tile, int row, int column) {
        // Check if a TilePosition already exists for the given game and tile
        TilePosition existingTilePosition = tilePositionRepository.findByAndWithGameAndTile(game, tile);

        if (existingTilePosition != null) {
            // Update the existing record with the new row and column
            existingTilePosition.setRowPosition(row);
            existingTilePosition.setColumnPosition(column);

            TilePosition updatedTilePosition = tilePositionRepository.save(existingTilePosition);
            log.info("TilePosition updated: {}, {}, {}, {}, {}",
                    existingTilePosition.getTile().getId(), existingTilePosition.getTile().getTileColor(),
                    existingTilePosition.getGame().getId(), existingTilePosition.getRowPosition(), existingTilePosition.getColumnPosition());

            return updatedTilePosition;
        } else {
            // Create a new TilePosition record if it doesn't exist
            TilePosition tilePosition = new TilePosition();
            tilePosition.setGame(game);
            tilePosition.setTile(tile);
            tilePosition.setRowPosition(row);
            tilePosition.setColumnPosition(column);

            TilePosition newTilePosition = tilePositionRepository.save(tilePosition);
            log.info("TilePosition created: {}, {}, {}, {}, {}",
                    newTilePosition.getTile().getNumberValue(), newTilePosition.getTile().getTileColor(),
                    newTilePosition.getGame().getId(), newTilePosition.getRowPosition(), newTilePosition.getColumnPosition());

            return newTilePosition;
        }
    }

}
