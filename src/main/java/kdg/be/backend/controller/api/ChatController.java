package kdg.be.backend.controller.api;

import kdg.be.backend.controller.dto.Chat.ChatMessageDTO;
import kdg.be.backend.controller.dto.Chat.ChatbotRequest;
import kdg.be.backend.controller.dto.Chat.ChatbotResponse;
import kdg.be.backend.domain.chatting.Chat;
import kdg.be.backend.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/sendMessage")
    public ChatbotResponse sendMessageToChatbot(@RequestBody ChatbotRequest request) {
        String answer = chatService.sendMessageToChatbot(request.threadId(), request.question());
        return new ChatbotResponse(answer);
    }

    @PostMapping("/createThread/{gameUserId}")
    public UUID createChatThread(@PathVariable UUID gameUserId) {
        return chatService.createChatThread(gameUserId);
    }

    @GetMapping("/api/chat/{chatId}/history")
    public List<ChatMessageDTO> getChatHistory(@PathVariable UUID chatId) {
        Map<String, Object> response = chatService.getChatHistory(chatId);

        List<Map<String, Object>> messages = (List<Map<String, Object>>) ((Map<String, Object>) response.get("values")).get("messages");

        return messages.stream()
                .map(message -> new ChatMessageDTO((String) message.get("content"), (String) message.get("type")))
                .toList();
    }
}
