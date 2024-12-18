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

    public MoveValidationService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public void isValidInitialMove(UUID playerId, PlayerMoveDeckDto playerMoveDeckDto) {
        // Retrieve the player's deck via the PlayerRepository
        Player player = playerRepository.findPlayerById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found for ID: " + playerId));
        List<Tile> deckTiles = player.getDeck().getTiles();


        // Calculate the total sum of the tile values in the player's deck
        int deckSum = deckTiles.stream()
                .mapToInt(Tile::getNumberValue)
                .sum();

        // Calculate the total sum of the tile values in the DTO
        int dtoSum = playerMoveDeckDto.tilesInDeck().stream()
                .mapToInt(PlayerMoveTileDto::numberValue)
                .sum();

        if ((deckSum - dtoSum) < 30) {
            throw new InvalidMoveException("You can not place a tileset with a total value of less than 30 in your first move.");
        }
    }
}
