package kdg.be.backend.service;


import kdg.be.backend.controller.dto.GameUserDto;
import kdg.be.backend.domain.GameUser;
import kdg.be.backend.domain.chatting.ChatHistory;
import kdg.be.backend.repository.GameUserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
public class GameUserService {

    private final GameUserRepository gameUserRepository;
    private final GameUserAchievementService gameUserAchievementService;

    public GameUserService(GameUserRepository gameUserRepository, GameUserAchievementService gameUserAchievementService) {
        this.gameUserRepository = gameUserRepository;
        this.gameUserAchievementService = gameUserAchievementService;
    }

    public void createGameUser(GameUserDto gameUserDto) {
        GameUser gameUser = new GameUser(gameUserDto.getId(), gameUserDto.getUsername());
        ChatHistory chatHistory = new ChatHistory(gameUser, new ArrayList<>());
        gameUser.setChatHistory(chatHistory);

        gameUserRepository.saveAndFlush(gameUser);
    }

    public boolean gameUserExists(UUID id, String username) {
        boolean exists = gameUserRepository.existsById(id);
        boolean existsByUsername = gameUserRepository.existsByUsername(username);
        return exists || existsByUsername;
    }


    public GameUser getGameUser(UUID uuid) {
        GameUser gameUser = gameUserRepository.findGameUserWithDetails(uuid);
        if (gameUser == null) {
            return null;
        }
        gameUser.setAchievements(gameUserAchievementService.getAchievementsForUser(gameUser.getId()));
        return gameUser;
    }
}
