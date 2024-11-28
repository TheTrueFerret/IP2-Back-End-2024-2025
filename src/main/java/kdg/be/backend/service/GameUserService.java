package kdg.be.backend.service;

import kdg.be.backend.domain.GameUser;
import kdg.be.backend.domain.chatting.ChatHistory;
import kdg.be.backend.repository.ChatHistoryRepository;
import kdg.be.backend.repository.GameUserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
public class GameUserService {

    GameUserRepository gameUserRepository;
    ChatHistoryRepository chatHistoryRepository;

    public GameUserService(GameUserRepository gameUserRepository, ChatHistoryRepository chatHistoryRepository) {
        this.gameUserRepository = gameUserRepository;
        this.chatHistoryRepository = chatHistoryRepository;
    }

    public void createGameUser(String username, String id) {
        GameUser gameUser = new GameUser(UUID.fromString(id), username);
        ChatHistory chatHistory = new ChatHistory(gameUser, new ArrayList<>());
        gameUser.setChatHistory(chatHistory);

        gameUserRepository.saveAndFlush(gameUser);
    }

    public boolean gameUserExists(UUID id) {
        return gameUserRepository.existsById(id);
    }
}
