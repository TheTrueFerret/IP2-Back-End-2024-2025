package kdg.be.backend.controller.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CreateLobbySettingsRequest(
        @Positive int minimumPlayers,
        @Positive int maximumPlayers,
        @NotBlank String joinCode
) {}