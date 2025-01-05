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
    public ResponseEntity<ChatbotResponse> sendMessageToChatbot(@RequestBody ChatbotRequest request) {
        ChatbotResponse response = chatService.sendMessageToChatbot(request.threadId(), request.question());
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/createThread/{gameUserId}")
    public ResponseEntity<UUID> createChatThread(@PathVariable UUID gameUserId) {
        UUID threadId = chatService.createChatThread(gameUserId);
        return ResponseEntity.status(201).body(threadId);
    }

    @GetMapping("/{chatId}/history")
    public ResponseEntity<?> getChatHistory(@PathVariable UUID chatId) {
        Map<String, Object> response = chatService.getChatHistory(chatId);

        List<Map<String, Object>> messages = (List<Map<String, Object>>) ((Map<String, Object>) response.get("values")).get("messages");

        if (messages == null) {
            return ResponseEntity.ok("There are no messages for this chat.");
        }

        List<ChatMessageDTO> chatMessages = messages.stream()
                .map(message -> new ChatMessageDTO((String) message.get("content"), (String) message.get("type")))
                .toList();

        return ResponseEntity.ok(chatMessages);
    }

    @GetMapping("/user/{userId}/chats")
    public ResponseEntity<List<UUID>> getAllChatIdsByUserId(@PathVariable UUID userId) {
        List<UUID> chatIds = chatService.getAllChatIdsByUserId(userId);
        return ResponseEntity.ok(chatIds);
    }

}
