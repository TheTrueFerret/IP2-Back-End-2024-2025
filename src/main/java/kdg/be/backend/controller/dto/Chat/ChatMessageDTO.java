package kdg.be.backend.controller.dto.Chat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ChatMessageDTO(String content, String type) {
}
