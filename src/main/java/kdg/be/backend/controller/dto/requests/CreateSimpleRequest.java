package kdg.be.backend.controller.dto.requests;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CreateSimpleRequest(
        @NotBlank UUID gameId,
        @NotBlank UUID playerId) {
}