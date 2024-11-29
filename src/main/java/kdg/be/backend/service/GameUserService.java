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

    GameUserRepository gameUserRepository;
    AchievementService achievementService;

    public GameUserService(GameUserRepository gameUserRepository, AchievementService achievementService) {
        this.gameUserRepository = gameUserRepository;
        this.achievementService = achievementService;
    }

    public void createGameUser(GameUserDto gameUserDto) {
        GameUser gameUser = new GameUser(gameUserDto.getId(), gameUserDto.getUsername());
        ChatHistory chatHistory = new ChatHistory(gameUser, new ArrayList<>());
        gameUser.setChatHistory(chatHistory);

        gameUserRepository.saveAndFlush(gameUser);
    }

    public boolean gameUserExists(UUID id) {
        return gameUserRepository.existsById(id);
    }


    public GameUser getGameUser(UUID uuid) {
        GameUser gameUser = gameUserRepository.findGameUserWithDetails(uuid);
        if (gameUser == null) {
            return null;
        }
        gameUser.setAchievements(achievementService.getAchievements(gameUser.getId()));
        return gameUser;
    }
}
