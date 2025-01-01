package kdg.be.backend.controller.api;

import kdg.be.backend.controller.dto.Chat.ChatbotRequest;
import kdg.be.backend.controller.dto.Chat.ChatbotResponse;
import kdg.be.backend.domain.chatting.Chat;
import kdg.be.backend.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
