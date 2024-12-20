package kdg.be.backend.service;


import kdg.be.backend.controller.dto.user.GameUserDto;
import kdg.be.backend.controller.dto.user.UserFriendDto;
import kdg.be.backend.domain.user.GameUser;
import kdg.be.backend.domain.chatting.ChatHistory;
import kdg.be.backend.domain.user.FriendRequest;
import kdg.be.backend.domain.user.RequestStatus;
import kdg.be.backend.exception.FriendRequestException;
import kdg.be.backend.exception.UserDoesNotExistException;
import kdg.be.backend.exception.UsersDoNotExistsException;
import kdg.be.backend.repository.FriendRequestRepository;
import kdg.be.backend.repository.GameRepository;
import kdg.be.backend.repository.GameUserRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameUserService {

    private final GameUserRepository gameUserRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final GameRepository gameRepository;
    private final GameUserAchievementService gameUserAchievementService;

    public GameUserService(GameUserRepository gameUserRepository, FriendRequestRepository friendRequestRepository, GameRepository gameRepository, GameUserAchievementService gameUserAchievementService) {
        this.gameUserRepository = gameUserRepository;
        this.friendRequestRepository = friendRequestRepository;
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

    //Accept friend request
    public boolean addFriend(UUID recieverId, String senderFriendUsername) {
        Optional<GameUser> optReceiver = gameUserRepository.findGameUserWithDetails(recieverId);
        Optional<GameUser> optSender = gameUserRepository.findGameUserByUsername(senderFriendUsername);
        FriendRequest friendRequest = null;
        if (optSender.isEmpty()) {
            throw new UserDoesNotExistException(recieverId.toString());
        } else if (optReceiver.isEmpty()) {
            throw new UserDoesNotExistException(senderFriendUsername);
        } else {
            friendRequest = friendRequestRepository.findFriendRequestBySenderAndReceiver(optSender.get().getId(), optReceiver.get().getId());
            GameUser sender = optSender.get();
            GameUser receiver = optReceiver.get();
            if (friendRequest.getStatus() == RequestStatus.PENDING) {
                addFriendToFriendList(receiver, senderFriendUsername);
                addFriendToFriendList(sender, receiver.getUsername());
                friendRequest.setStatus(RequestStatus.ACCEPTED);
                friendRequestRepository.saveAndFlush(friendRequest);
                return true;
            }
            throw new FriendRequestException("Friend request is not pending");
        }
    }

    //Creation of a friend request
    public boolean addFriendRequest(UUID id, String friendUsername) {
        Optional<GameUser> optSender = gameUserRepository.findGameUserWithDetails(id);
        Optional<GameUser> optReceiver = gameUserRepository.findGameUserByUsername(friendUsername);
        if (optSender.isEmpty()) {
            throw new UserDoesNotExistException(id.toString());
        } else if (optReceiver.isEmpty()) {
            throw new UserDoesNotExistException(friendUsername);
        } else {
            GameUser sender = optSender.get();
            GameUser receiver = optReceiver.get();
            FriendRequest friendRequest = new FriendRequest(sender, receiver, RequestStatus.PENDING);
            friendRequestRepository.saveAndFlush(friendRequest);
        }
        return true;
    }

    public String getFriendRequests(UUID id) {
        Optional<List<FriendRequest>> optionalGameUser = friendRequestRepository.findFriendRequestsByReceiver_Id(id);
        if (optionalGameUser.get().isEmpty()) {
            throw new FriendRequestException("No friend requests found");
        }
        List<FriendRequest> friendRequests = optionalGameUser.get();
        StringBuilder stringBuilder = new StringBuilder();
        for (FriendRequest friendRequest : friendRequests) {
            stringBuilder.append(friendRequest.getSender().getUsername()).append(" ");
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
        Optional<GameUser> optFriend = gameUserRepository.findGameUserByUsername(friendName);
        if (optFriend.isPresent()) {
            GameUser friend = optFriend.get();
            List<GameUser> friendList = user.getFriendList();
            friendList.add(friend);
            user.setFriendList(friendList);
            gameUserRepository.saveAndFlush(user);
        }
    }

    public List<GameUserDto> getGameUsers() {
        List<GameUser> gameUsers = gameUserRepository.findAllWithAchievements();
        if (gameUsers.isEmpty()) {
            throw new UsersDoNotExistsException("No users found");
        }
        List<GameUserDto> gameUserDtos = new ArrayList<>();
        for (GameUser gameUser : gameUsers) {
            GameUserDto dto = new GameUserDto(gameUser, gameUser.getAchievements());
            gameUserDtos.add(dto);
        }
        return gameUserDtos;
    }

    public List<UserFriendDto> getGameUsersWithName(UUID userId, String username) {
        List<GameUser> gameUser = gameUserRepository.findGameUsersByUsernameIsContainingIgnoreCase(username);
        if (gameUser.isEmpty()) {
            throw new UsersDoNotExistsException("No users found with name " + username);
        }
        List<UserFriendDto> userFriendDto = new ArrayList<>();
        for (GameUser user : gameUser) {
            userFriendDto.add(new UserFriendDto(user, isFriend(userId, user)));
        }
        return userFriendDto;
    }

    public boolean isFriend(UUID userId, GameUser user) {
        GameUser gameUser = gameUserRepository.findGameUserWithDetails(userId).orElse(null);
        if (gameUser != null) {
            List<GameUser> friendList = gameUser.getFriendList();
            return friendList.stream().findAny().filter(friend -> friend.getId().equals(user.getId())).isPresent();
        }
        return false;
    }
}
