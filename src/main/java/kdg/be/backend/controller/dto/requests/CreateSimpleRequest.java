package kdg.be.backend.controller.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateSimpleRequest(
        @NotNull UUID gameId,
        @NotNull UUID playerId) {
}