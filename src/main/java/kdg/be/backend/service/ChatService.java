package kdg.be.backend.service;

import kdg.be.backend.domain.chatting.Chat;
import kdg.be.backend.repository.ChatRepository;
import kdg.be.backend.repository.GameUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ChatService {
    private final ChatRepository chatRepository;
    private final GameUserRepository gameUserRepository;
    private final RestTemplate restTemplate;

    public ChatService(ChatRepository chatRepository, RestTemplate restTemplate, GameUserRepository gameUserRepository) {
        this.chatRepository = chatRepository;
        this.restTemplate = restTemplate;
        this.gameUserRepository = gameUserRepository;
    }

    public UUID createChatThread(UUID gameUserId) {
        UUID threadId = restTemplate.postForObject("http://localhost:8000/api/chatbot/thread", null, UUID.class);

        Chat chat = new Chat();
        chat.setId(threadId);
        chat.setGameUser(gameUserRepository.findById(gameUserId).orElseThrow(() -> new IllegalArgumentException("GameUser not found for ID: " + gameUserId)));
        chatRepository.save(chat);

        return threadId;
    }

    public String sendMessageToChatbot(UUID chatId, String message) {
        chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found for ID: " + chatId));

        var request = Map.of("question", message, "thread_id", chatId);

        var response = restTemplate.postForObject("http://localhost:8000/api/chatbot/", request, Map.class);

        if (response == null) {
            throw new IllegalArgumentException("Chatbot response is null");
        }
        return (String) response.get("answer");
    }

    public Map<String, Object> getChatHistory(UUID chatId) {
        chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found for ID: " + chatId));

        String url = "http://localhost:8123/threads/" + chatId + "/state";
        return restTemplate.getForObject(url, Map.class);
    }
}
