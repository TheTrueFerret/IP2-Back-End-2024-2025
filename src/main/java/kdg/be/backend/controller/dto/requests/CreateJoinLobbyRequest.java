package kdg.be.backend.controller.dto.requests;

import jakarta.validation.constraints.NotBlank;

public record CreateJoinLobbyRequest(
        @NotBlank String joinCode
) {}