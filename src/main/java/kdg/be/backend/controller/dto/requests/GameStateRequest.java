package kdg.be.backend.controller.dto.requests;

import kdg.be.backend.domain.Tile;

import java.util.List;
import java.util.UUID;

public record GameStateRequest(UUID userId,UUID gameId, List<Tile> playerDeck,
                               List<List<Tile>> playingField) {
}
