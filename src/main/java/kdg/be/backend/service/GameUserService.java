package kdg.be.backend.service;


import kdg.be.backend.controller.dto.customization.CustomizableDto;
import kdg.be.backend.controller.dto.user.FriendRequestDto;
import kdg.be.backend.controller.dto.user.GameUserDto;
import kdg.be.backend.controller.dto.user.UserFriendDto;
import kdg.be.backend.domain.user.FriendRequest;
import kdg.be.backend.domain.user.GameUser;
import kdg.be.backend.domain.user.RequestStatus;
import kdg.be.backend.exception.FriendException;
import kdg.be.backend.exception.FriendRequestException;
import kdg.be.backend.exception.UserDoesNotExistException;
import kdg.be.backend.exception.UsersDoNotExistsException;
import kdg.be.backend.repository.FriendRequestRepository;
import kdg.be.backend.repository.GameRepository;
import kdg.be.backend.repository.GameUserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public boolean addFriend(UUID recieverId, String requestId) {
        //Search friend request
        FriendRequest friendRequest = friendRequestRepository.findById(UUID.fromString(requestId))
                .orElseThrow(() -> new FriendRequestException("Friend request not found"));

        GameUser sender = gameUserRepository.findGameUserWithDetails(friendRequest.getSender().getId()).orElseThrow(() -> new UserDoesNotExistException(friendRequest.getSender().getId().toString()));
        GameUser receiver = gameUserRepository.findGameUserWithDetails(recieverId).orElseThrow(() -> new UserDoesNotExistException(recieverId.toString()));
        if (friendRequest.getStatus() == RequestStatus.PENDING) {
            addFriendToFriendList(receiver, sender.getUsername());
            addFriendToFriendList(sender, receiver.getUsername());
            friendRequest.setStatus(RequestStatus.ACCEPTED);
            friendRequestRepository.saveAndFlush(friendRequest);
            return true;
        }
        throw new FriendRequestException("Friend request is not pending");
    }

    public boolean declineFriendRequest(UUID userId, String requestId) {
        FriendRequest friendRequest = friendRequestRepository.findById(UUID.fromString(requestId)).orElseThrow(() -> new FriendRequestException("Friend request not found"));

        if (friendRequest.getStatus() == RequestStatus.PENDING || friendRequest.getReceiver() == getGameUser(userId)) {
            friendRequest.setStatus(RequestStatus.DECLINED);
            friendRequestRepository.saveAndFlush(friendRequest);
            return true;
        }
        throw new FriendRequestException("User not allowed to decline friend request.");
    }

    //Creation of a friend request
    public boolean addFriendRequest(UUID id, String friendUsername) {
        GameUser sender = gameUserRepository.findGameUserWithDetails(id).orElseThrow(() -> new UserDoesNotExistException(id.toString()));
        GameUser receiver = gameUserRepository.findGameUserByUsername(friendUsername).orElseThrow(() -> new UserDoesNotExistException(friendUsername));

        FriendRequest existingFriendRequest = friendRequestRepository.findFriendRequestBySenderAndReceiver(sender.getId(), receiver.getId());
        if (existingFriendRequest != null) {
            throw new FriendRequestException("Friend request already exists");
        }

        FriendRequest friendRequest = new FriendRequest(sender, receiver, RequestStatus.PENDING);
        friendRequestRepository.saveAndFlush(friendRequest);
        return true;
    }

    public List<FriendRequestDto> getFriendRequests(UUID id) {
        List<FriendRequest> friendRequests = friendRequestRepository.findFriendRequestsByReceiver_Id(id).orElseThrow(() -> new FriendRequestException("No friend requests found"));
        if (friendRequests.isEmpty()) {
            throw new FriendRequestException("No friend requests found");
        }

        List<FriendRequestDto> friendRequestDtos = new ArrayList<>();
        for (FriendRequest friendRequest : friendRequests) {
            if (friendRequest.getStatus() == RequestStatus.PENDING) {
                friendRequestDtos.add(new FriendRequestDto(friendRequest.getId().toString(), friendRequest.getSender().getUsername(), friendRequest.getSender().getId().toString()));
            }
        }
        return friendRequestDtos;
    }

    public void updateGameUserFriendList(UUID userId, List<GameUser> friendList) {
        GameUser gameUser = gameUserRepository.findById(userId).orElseThrow(() -> new UserDoesNotExistException(userId.toString()));
        gameUser.setFriendList(friendList);
        gameUserRepository.saveAndFlush(gameUser);
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


    //Usage of Pageable zodat er maar 20 resultaten worden getoond
    //Zorgt ervoor dat niet heel de db wordt geladen in memory
    public List<UserFriendDto> getGameUsersWithName(UUID userId, String username) {
        List<GameUserDto> users;

        if (!username.isEmpty() || username.isBlank()) {
            users = gameUserRepository.findGameUsersByUsernameIsContainingIgnoreCase(username)
                    .stream().limit(20)
                    .map(GameUserDto::new)
                    .collect(Collectors.toList());
        } else {
            users = gameUserRepository.findAll()
                    .stream().limit(20)
                    .map(GameUserDto::new)
                    .collect(Collectors.toList());
        }
        List<UserFriendDto> userFriendDtos = users.stream().map(user -> new UserFriendDto(getGameUser(user.getId()), isFriend(userId, getGameUser(user.getId())))).collect(Collectors.toList());
        return userFriendDtos;
    }

    public boolean isFriend(UUID userId, GameUser user) {
        GameUser gameUser = gameUserRepository.findGameUserWithDetails(userId).orElseThrow(() -> new UserDoesNotExistException(userId.toString()));

        List<GameUser> friendList = gameUser.getFriendList();
        return friendList.stream().findAny().filter(friend -> friend.getId().equals(user.getId())).isPresent();
    }

    public List<UserFriendDto> getFriends(UUID userId) {
        GameUser gameUser = gameUserRepository.findGameUserWithDetails(userId).orElseThrow(() -> new UserDoesNotExistException(userId.toString()));

        List<GameUser> friendList = gameUser.getFriendList();
        List<UserFriendDto> userFriendDtos = new ArrayList<>();
        for (GameUser friend : friendList) {
            userFriendDtos.add(new UserFriendDto(friend, true));
        }
        return userFriendDtos;
    }

    public List<CustomizableDto> getCustomizables(UUID userId) {
        GameUser gameUser = gameUserRepository.findGameUserByIdWithCustomizables(userId).orElseThrow(() -> new UserDoesNotExistException(userId.toString()));
        List<CustomizableDto> customizables = gameUser.getCustomizables().stream().map(customizable -> new CustomizableDto(customizable.getName(), customizable.getDescription(), customizable.getColor(), customizable.getPoints())).collect(Collectors.toList());
        if (customizables.isEmpty()) {
            return new ArrayList<CustomizableDto>();
        }
        return customizables;
    }

    public Integer getUserPoints(UUID userId) {
        GameUser gameUser = gameUserRepository.findGameUserByIdWithCustomizables(userId).orElseThrow(() -> new UserDoesNotExistException(userId.toString()));
        return gameUser.getPoints();
    }

    public boolean removeFriend(UUID userId, String friendId) {
        GameUser gameUser = gameUserRepository.findGameUserWithDetails(UUID.fromString(friendId)).orElseThrow(() -> new UserDoesNotExistException(friendId));
        GameUser friendUser = gameUserRepository.findGameUserWithDetails(userId).orElseThrow(() -> new UserDoesNotExistException(userId.toString()));

        if (isFriend(userId, gameUser)) {
            removeFriendFromFriendList(friendUser, gameUser);
            removeFriendFromFriendList(gameUser, friendUser);
            removeFriendRequest(userId, friendId);
            return true;
        }
        return false;
    }

    public void removeFriendFromFriendList(GameUser user, GameUser friend) {
        List<GameUser> friendList = user.getFriendList();
        if (friendList.isEmpty()) {
            throw new FriendException("Friend list is empty");
        }
        if (friendList.stream().anyMatch(f -> f.getId().equals(friend.getId()))) {
            GameUser friendToRemove = friendList.stream().filter(f -> f.getId().equals(friend.getId())).findFirst().orElseThrow(() -> new FriendException("Friend not found in friend list"));
            friendList.remove(friendToRemove);
            user.setFriendList(friendList);
            gameUserRepository.saveAndFlush(user);
            return;
        }
        throw new FriendException("Friend not found in friend list");
    }

    public void removeFriendRequest(UUID userId, String friendId) {
        GameUser user = gameUserRepository.findGameUserWithDetails(UUID.fromString(friendId))
                .orElseThrow(() -> new UserDoesNotExistException(friendId));
        GameUser friend = gameUserRepository.findGameUserWithDetails(userId)
                .orElseThrow(() -> new UserDoesNotExistException(userId.toString()));

        FriendRequest friendRequest = friendRequestRepository.findFriendRequestBySenderAndReceiver(user.getId(), friend.getId());
        if (friendRequest == null) {
            friendRequest = friendRequestRepository.findFriendRequestBySenderAndReceiver(friend.getId(), user.getId());
        }
        if (friendRequest == null) {
            throw new FriendRequestException("Friend request not found");
        }
        friendRequestRepository.delete(friendRequest);
    }
}
