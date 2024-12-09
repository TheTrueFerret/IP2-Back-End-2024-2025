package kdg.be.backend.service;


import kdg.be.backend.controller.dto.GameUserDto;
import kdg.be.backend.domain.user.FriendRequest;
import kdg.be.backend.domain.user.GameUser;
import kdg.be.backend.domain.chatting.ChatHistory;
import kdg.be.backend.domain.user.RequestStatus;
import kdg.be.backend.repository.FriendRequestRepository;
import kdg.be.backend.repository.GameUserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class GameUserService {

    private final GameUserRepository gameUserRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final GameUserAchievementService gameUserAchievementService;

    public GameUserService(GameUserRepository gameUserRepository, FriendRequestRepository friendRequestRepository, GameUserAchievementService gameUserAchievementService) {
        this.gameUserRepository = gameUserRepository;
        this.friendRequestRepository = friendRequestRepository;
        this.gameUserAchievementService = gameUserAchievementService;
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
        gameUser.setAchievements(gameUserAchievementService.getAchievementsForUser(gameUser.getId()));
        return gameUser;
    }

    public boolean addFriend(UUID id, String friendUsername) {
        GameUser receiver = gameUserRepository.findGameUserWithDetails(id);
        GameUser sender = gameUserRepository.findGameUserByUsername(friendUsername);
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
        GameUser sender = gameUserRepository.findGameUserWithDetails(id);
        GameUser receiver = gameUserRepository.findGameUserByUsername(friendUsername);
        if (sender == null || receiver == null) {
            return false;
        } else {
            FriendRequest friendRequest = new FriendRequest(sender, receiver, RequestStatus.PENDING);
            friendRequestRepository.saveAndFlush(friendRequest);
        }
        return true;
    }

    public String getFriendRequests(UUID id) {
        GameUser gameUser = gameUserRepository.findGameUserWithDetails(id);
        if (gameUser == null) {
            return "";
        }
        List<GameUser> friends = gameUser.getFriendList();
        StringBuilder stringBuilder = new StringBuilder();
        for (GameUser friend : friends) {
            stringBuilder.append(friend.getUsername()).append("\n");
        }
        return stringBuilder.toString();
    }

    public void updateGameUserFriendList(UUID userId, List<GameUser> friendList) {
        GameUser gameUser = gameUserRepository.findById(userId).orElse(null);
        if (gameUser != null) {
            gameUser.setFriendList(friendList);
            gameUserRepository.saveAndFlush(gameUser);
        }
    }

    public void addFriendToFriendList(GameUser user, String friendName) {
        GameUser friend = gameUserRepository.findGameUserByUsername(friendName);
        if (friend != null) {
            List<GameUser> friendList = user.getFriendList();
            friendList.add(friend);
            user.setFriendList(friendList);
            gameUserRepository.saveAndFlush(user);
        }
    }
}
