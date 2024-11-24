package kdg.be.backend.controller.dto;

import java.util.UUID;

public record PlayerDto(UUID id, String username, UUID gameId, DeckDto deckDto) { }