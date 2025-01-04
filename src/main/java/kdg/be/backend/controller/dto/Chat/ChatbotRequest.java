package kdg.be.backend.controller.dto.Chat;

import java.util.UUID;

public record ChatbotRequest(String question, UUID threadId) {
}
