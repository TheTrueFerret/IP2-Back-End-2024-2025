package kdg.be.backend.controller.dto.player;

import kdg.be.backend.controller.dto.game.DeckDto;

import java.util.UUID;

public record PlayerDto(UUID id, String username, int score, UUID gameId, DeckDto deckDto) { }