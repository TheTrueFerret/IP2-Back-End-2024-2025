package kdg.be.backend.service;

import kdg.be.backend.controller.dto.requests.PlayerMoveDeckDto;
import kdg.be.backend.controller.dto.requests.PlayerMoveTileSetDto;
import kdg.be.backend.controller.dto.requests.PlayerMoveTileDto;
import kdg.be.backend.domain.Player;
import kdg.be.backend.domain.Tile;
import kdg.be.backend.exception.InvalidMoveException;
import kdg.be.backend.repository.PlayerRepository;
import kdg.be.backend.repository.TileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
