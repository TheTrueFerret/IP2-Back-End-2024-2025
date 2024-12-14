package kdg.be.backend.service;


import kdg.be.backend.controller.dto.GameUserDto;
import kdg.be.backend.domain.GameUser;
import kdg.be.backend.domain.chatting.ChatHistory;
import kdg.be.backend.domain.user.FriendRequest;
import kdg.be.backend.domain.user.GameUser;
import kdg.be.backend.domain.user.RequestStatus;
import kdg.be.backend.repository.GameRepository;
import kdg.be.backend.repository.GameUserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class GameUserService {

    private final GameUserRepository gameUserRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final GameRepository gameRepository;
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
    public boolean addFriend(UUID id, String friendUsername) {
        kdg.be.backend.domain.user.GameUser receiver = gameUserRepository.findGameUserWithDetails(id);
        kdg.be.backend.domain.user.GameUser sender = gameUserRepository.findGameUserByUsername(friendUsername);
        FriendRequest friendRequest = null;
        if (sender == null || receiver == null) {
            return false;
        } else {
            friendRequest = friendRequestRepository.findFriendRequestBySenderAndReceiver(sender.getId(), receiver.getId());
        }
        if (friendRequest.getStatus() == RequestStatus.PENDING) {
            addFriendToFriendList(receiver, friendUsername);
            addFriendToFriendList(sender, receiver.getUsername());
            friendRequest.setStatus(RequestStatus.ACCEPTED);
            friendRequestRepository.saveAndFlush(friendRequest);
            return true;
        }
        return false;
    }

    public boolean addFriendRequest(UUID id, String friendUsername) {
        kdg.be.backend.domain.user.GameUser sender = gameUserRepository.findGameUserWithDetails(id);
        kdg.be.backend.domain.user.GameUser receiver = gameUserRepository.findGameUserByUsername(friendUsername);
        if (sender == null || receiver == null) {
            return false;
        } else {
            FriendRequest friendRequest = new FriendRequest(sender, receiver, RequestStatus.PENDING);
            friendRequestRepository.saveAndFlush(friendRequest);
        }
        return true;
    }

    public String getFriendRequests(UUID id) {
        kdg.be.backend.domain.user.GameUser gameUser = gameUserRepository.findGameUserWithDetails(id);
        if (gameUser == null) {
            return "";
        }
        List<kdg.be.backend.domain.user.GameUser> friends = gameUser.getFriendList();
        StringBuilder stringBuilder = new StringBuilder();
        for (kdg.be.backend.domain.user.GameUser friend : friends) {
            stringBuilder.append(friend.getUsername()).append("\n");
        }
        return stringBuilder.toString();
    }

    public void updateGameUserFriendList(UUID userId, List<kdg.be.backend.domain.user.GameUser> friendList) {
        kdg.be.backend.domain.user.GameUser gameUser = gameUserRepository.findById(userId).orElse(null);
        if (gameUser != null) {
            gameUser.setFriendList(friendList);
            gameUserRepository.saveAndFlush(gameUser);
        }
    }

    public void addFriendToFriendList(kdg.be.backend.domain.user.GameUser user, String friendName) {
        kdg.be.backend.domain.user.GameUser friend = gameUserRepository.findGameUserByUsername(friendName);
        if (friend != null) {
            List<kdg.be.backend.domain.user.GameUser> friendList = user.getFriendList();
            friendList.add(friend);
            user.setFriendList(friendList);
            gameUserRepository.saveAndFlush(user);
        }
    }
}
