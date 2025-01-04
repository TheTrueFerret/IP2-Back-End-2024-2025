package kdg.be.backend.controller.dto.Chat;

import java.util.UUID;

public record ChatbotResponse(String answer, UUID threadId) {
}
