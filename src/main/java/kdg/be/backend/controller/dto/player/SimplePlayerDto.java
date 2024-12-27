package kdg.be.backend.controller.dto.player;

import java.util.UUID;

public record SimplePlayerDto(UUID id, String username, int score, UUID gameId) { }