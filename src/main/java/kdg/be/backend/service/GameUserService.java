package kdg.be.backend.service;


import jdk.jshell.spi.ExecutionControl;
import kdg.be.backend.controller.dto.GameUserDto;
import kdg.be.backend.domain.GameUser;
import kdg.be.backend.domain.chatting.ChatHistory;
import kdg.be.backend.exception.UserDoesNotExistException;
import kdg.be.backend.repository.GameRepository;
import kdg.be.backend.repository.GameUserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
public class GameUserService {

    private final GameUserRepository gameUserRepository;
    private final GameRepository gameRepository;
    private final GameUserAchievementService gameUserAchievementService;

    public GameUserService(GameUserRepository gameUserRepository, GameRepository gameRepository, GameUserAchievementService gameUserAchievementService) {
        this.gameUserRepository = gameUserRepository;
        this.gameRepository = gameRepository;
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
        GameUser gameUser = gameUserRepository.findGameUserWithDetails(uuid).orElseThrow(() -> new UserDoesNotExistException(uuid.toString()));
        gameUser.setAchievements(gameUserAchievementService.getAchievementsForUser(gameUser.getId()));
        return gameUser;
    }

    public int getGamesPlayed(UUID userId) {
        return gameRepository.countGamesByPlayersGameUserId(userId);
    }

    public int getGamesWon(UUID userId) {
        //TODO: implement this method when winning is implemented
        return 0;
    }
}
