package kdg.be.backend.service;

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

    public void isValidInitialMove(UUID playerId, List<PlayerMoveTileDto> deckDto) {
        Player player = playerRepository.findPlayerById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found for ID: " + playerId));
        List<Tile> deckTiles = player.getDeck().getTiles();


        int deckSum = deckTiles.stream()
                .mapToInt(Tile::getNumberValue)
                .sum();

        int dtoSum = deckDto.stream()
                .mapToInt(PlayerMoveTileDto::numberValue)
                .sum();

        validateTileAttributes(deckDto);

        if ((deckSum - dtoSum) < 30) {
            throw new InvalidMoveException("You can not place a tileset with a total value of less than 30 in your first move.");
        }
    }

    public void validateTileAttributes(List<PlayerMoveTileDto> deckDto) {
        List<Tile> tilesFromDb = deckDto.stream()
                .map(dto -> tileRepository.findById(dto.id())
                        .orElseThrow(() -> new IllegalArgumentException("Tile not found for ID: " + dto.id())))
                .collect(Collectors.toList());

        for (Tile tileFromDb : tilesFromDb) {
            PlayerMoveTileDto tileDto = deckDto.stream()
                    .filter(dto -> dto.id().equals(tileFromDb.getId()))
                    .findFirst()
                    .orElseThrow(() -> new InvalidMoveException("Tile ID does not exist: " + tileFromDb.getId()));

            if (tileFromDb.getNumberValue() != tileDto.numberValue() || !tileFromDb.getTileColor().equals(tileDto.color())) {
                throw new InvalidMoveException("Tile attributes do not match for tile ID: " + tileDto.id());
            }
        }
    }
}
