package kdg.be.backend.controller.dto.requests;

import jakarta.validation.constraints.Positive;

public record CreateGameSettingsRequest(
        @Positive int roundTime,
        @Positive int startTileAmount
) {
}