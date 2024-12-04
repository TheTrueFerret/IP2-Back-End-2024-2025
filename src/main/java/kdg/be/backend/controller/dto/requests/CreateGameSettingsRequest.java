package kdg.be.backend.controller.dto.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record CreateGameSettingsRequest(
        @Positive int turnTime,
        @Positive int startTileAmount,
        @NotNull UUID hostUserId) {
}