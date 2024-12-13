package kdg.be.backend.controller.dto.requests;

import java.util.List;

public record PlayerMoveDeckDto(List<PlayerMoveTileDto> tilesInDeck) {
}
