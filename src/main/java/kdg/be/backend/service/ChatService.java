package kdg.be.backend.service;

import kdg.be.backend.controller.dto.Chat.ChatbotResponse;
import kdg.be.backend.domain.chatting.Chat;
import kdg.be.backend.repository.ChatRepository;
import kdg.be.backend.repository.GameUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ChatService {
    private final ChatRepository chatRepository;
    private final GameUserRepository gameUserRepository;
    private final RestTemplate restTemplate;

    @Value("${chatbot.api.base-url}")
    private String chatbotApiBaseUrl;

    @Value("${chatbot.api.thread-url}")
    private String chatbotApiThreadUrl;

    @Value("${chatbot.api.history-url}")
    private String chatbotApiHistoryUrl;

    public ChatService(ChatRepository chatRepository, RestTemplate restTemplate, GameUserRepository gameUserRepository) {
        this.chatRepository = chatRepository;
        this.restTemplate = restTemplate;
        this.gameUserRepository = gameUserRepository;
    }

    public UUID createChatThread(UUID gameUserId) {
        UUID threadId = restTemplate.postForObject(chatbotApiThreadUrl, null, UUID.class);

        Chat chat = new Chat();
        chat.setId(threadId);
        chat.setGameUser(gameUserRepository.findById(gameUserId).orElseThrow(() -> new IllegalArgumentException("GameUser not found for ID: " + gameUserId)));
        chatRepository.save(chat);

        return threadId;
    }

    public ChatbotResponse sendMessageToChatbot(UUID chatId, String message) {
        chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found for ID: " + chatId));

        var request = Map.of("question", message, "thread_id", chatId);

        var response = restTemplate.postForObject(chatbotApiBaseUrl, request, Map.class);

        if (response == null) {
            throw new IllegalArgumentException("Chatbot response is null");
        }

        UUID responseThreadId = UUID.fromString((String) response.get("thread_id"));
        if (!responseThreadId.equals(chatId)) {
            throw new IllegalArgumentException("Thread ID in response does not match the given chat ID");
        }

        String answer = (String) response.get("answer");

        return new ChatbotResponse(answer, responseThreadId);
    }

    public Map<String, Object> getChatHistory(UUID chatId) {
        chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found for ID: " + chatId));

        String url = chatbotApiHistoryUrl + "/" + chatId + "/state";
        return restTemplate.getForObject(url, Map.class);
    }

    public List<UUID> getAllChatIdsByUserId(UUID userId) {
        return chatRepository.findAllByGameUserId(userId)
                .stream()
                .map(Chat::getId)
                .collect(Collectors.toList());
    }

}
