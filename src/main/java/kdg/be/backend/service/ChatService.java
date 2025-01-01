package kdg.be.backend.service;

import kdg.be.backend.domain.chatting.Chat;
import kdg.be.backend.repository.ChatRepository;
import kdg.be.backend.repository.GameUserRepository;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@Service
public class ChatService {
    private final ChatRepository chatRepository;
    private final GameUserRepository gameUserRepository;
    private final WebClient webClient;

    public ChatService(ChatRepository chatRepository, WebClient.Builder webClientBuilder, GameUserRepository gameUserRepository) {
        this.chatRepository = chatRepository;
        this.gameUserRepository = gameUserRepository;
        this.webClient = webClientBuilder.baseUrl("http://localhost:8000").clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create().responseTimeout(Duration.ofMinutes(30)) // Set timeout
                ))
                .build();
    }

    public UUID createChatThread(UUID gameUserId) {
        // Call the chatbot to create a new thread
        UUID threadId = webClient.post()
                .uri("/api/chatbot/thread")
                .retrieve()
                .bodyToMono(UUID.class)
                .block();

        // Save the new chat thread in the database
        Chat chat = new Chat();
        chat.setId(threadId);
        chat.setGameUser(gameUserRepository.findById(gameUserId).orElseThrow(() -> new IllegalArgumentException("GameUser not found for ID: " + gameUserId)));
        chatRepository.save(chat);

        return threadId;
    }

    public String sendMessageToChatbot(UUID chatId, String message) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found for ID: " + chatId));

        var request = Map.of("question", message, "thread_id", chatId);

        var response = webClient.post()
                .uri("/api/chatbot/")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return (String) response.get("answer");
    }
}
