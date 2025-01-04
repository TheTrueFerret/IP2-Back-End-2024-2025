package kdg.be.backend.service;

import kdg.be.backend.controller.dto.requests.PlayerMoveDeckDto;
import kdg.be.backend.controller.dto.requests.PlayerMoveTileDto;
import kdg.be.backend.domain.Player;
import kdg.be.backend.domain.Tile;
import kdg.be.backend.exception.InvalidMoveException;
import kdg.be.backend.exception.TileSetException;
import kdg.be.backend.repository.PlayerRepository;
import kdg.be.backend.repository.TileRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MoveValidationService {
    private final PlayerRepository playerRepository;
    private final TileRepository tileRepository;

    public MoveValidationService(PlayerRepository playerRepository, TileRepository tileRepository) {
        this.playerRepository = playerRepository;
        this.tileRepository = tileRepository;
    }

    public void isValidInitialMove(UUID playerId, PlayerMoveDeckDto playerMoveDeckDto) {
        Player player = playerRepository.findPlayerById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found for ID: " + playerId));
        List<Tile> deckTiles = player.getDeck().getTiles();


        int deckSum = deckTiles.stream()
                .mapToInt(Tile::getNumberValue)
                .sum();

        int dtoSum = playerMoveDeckDto.tilesInDeck().stream()
                .mapToInt(PlayerMoveTileDto::numberValue)
                .sum();

        validateTileAttributes(playerMoveDeckDto);

        if ((deckSum - dtoSum) < 30) {
            throw new InvalidMoveException("You can not place a tileset with a total value of less than 30 in your first move.");
        }
    }

    public void validateTileAttributes(PlayerMoveDeckDto playerMoveDeckDto) {
        List<Tile> tilesFromDb = playerMoveDeckDto.tilesInDeck().stream()
                .map(dto -> tileRepository.findById(dto.tileId())
                        .orElseThrow(() -> new IllegalArgumentException("Tile not found for ID: " + dto.tileId())))
                .collect(Collectors.toList());

        for (Tile tileFromDb : tilesFromDb) {
            PlayerMoveTileDto tileDto = playerMoveDeckDto.tilesInDeck().stream()
                    .filter(dto -> dto.tileId().equals(tileFromDb.getId()))
                    .findFirst()
                    .orElseThrow(() -> new InvalidMoveException("Tile ID does not exist: " + tileFromDb.getId()));

            if (tileFromDb.getNumberValue() != tileDto.numberValue() || !tileFromDb.getTileColor().equals(tileDto.color())) {
                throw new InvalidMoveException("Tile attributes do not match for tile ID: " + tileDto.tileId());
            }
        }
    }

    public void checkTileSet(List<PlayerMoveTileDto> tileGroup) {
        if (tileGroup.size() < 3) {
            throw new TileSetException("The tile set must contain at least 3 tiles.");
        }

        for (int i = 0; i < tileGroup.size(); i++) {
            // Skip the first tile
            if (i > 0) {
                PlayerMoveTileDto currentTile = tileGroup.get(i);
                PlayerMoveTileDto previousTile = tileGroup.get(i - 1);

                //If last tile is a joker, skip check
                if (i == tileGroup.size() - 1 && currentTile.numberValue() == 0 || previousTile.numberValue() == 0) {
                    continue;
                }

                if (currentTile.numberValue() == 0) {
                    // Check if joker is last tile
                    if (i < tileGroup.size() - 1) {
                        PlayerMoveTileDto nextTile = tileGroup.get(i + 1);

                        // Jokers bridging a sequence for the same color tiles
                        if (nextTile.color().equals(previousTile.color())) {
                            if (nextTile.numberValue() != previousTile.numberValue() + 2) {
                                throw new TileSetException("Joker does not correctly bridge a sequence for tiles of the same color.");
                            }
                        }
                        // Jokers matching numbers between different colors tiles
                        else {
                            if (nextTile.numberValue() != previousTile.numberValue()) {
                                throw new TileSetException("Joker does not correctly match numbers between different colors.");
                            }
                        }
                    }
                    continue;
                }


                if (currentTile.color().equals(previousTile.color())) {
                    if (currentTile.numberValue() != previousTile.numberValue() + 1) {
                        throw new TileSetException("Tiles of the same color must be in sequential order.");
                    }
                } else {
                    if (currentTile.numberValue() != previousTile.numberValue()) {
                        throw new TileSetException("Tiles of different colors must have the same number.");
                    }
                }
            }
        }
    }
}
