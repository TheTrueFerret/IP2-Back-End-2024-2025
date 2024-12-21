package kdg.be.backend.controller.dto;

import java.util.UUID;

public record PlayerDto(UUID id, String username, int score, UUID gameId, DeckDto deckDto) { }