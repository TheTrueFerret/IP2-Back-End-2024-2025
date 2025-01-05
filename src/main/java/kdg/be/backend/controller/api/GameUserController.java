package kdg.be.backend.controller.api;

import kdg.be.backend.controller.dto.game.GameUserAchievementDto;
import kdg.be.backend.controller.dto.user.FriendRequestDto;
import kdg.be.backend.controller.dto.user.GameUserDto;
import kdg.be.backend.controller.dto.user.UserFriendDto;
import kdg.be.backend.domain.user.GameUserAchievement;
import kdg.be.backend.service.GameUserAchievementService;
import kdg.be.backend.service.GameUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.logging.Level.INFO;

@RestController
@RequestMapping("/api/gameuser")
public class GameUserController {

    private final GameUserService gameUserService;
    private final GameUserAchievementService gameUserAchievementService;
    private final Logger logger = Logger.getLogger(GameUserController.class.getName());

    public GameUserController(GameUserService gameUserService, GameUserAchievementService gameUserAchievementService) {
        this.gameUserService = gameUserService;
        this.gameUserAchievementService = gameUserAchievementService;
    }

    @PostMapping("/user")
    public ResponseEntity<String> createGameUser(@RequestBody Map<String, String> userData) {
        String id = userData.get("id") != null ? userData.get("id") : null;
        String username = userData.get("username") != null ? userData.get("username") : null;
        if (id == null || username == null) {
            logger.warning("Invalid game user data");
            return ResponseEntity.badRequest().body("Invalid game user data");
        } else if (gameUserService.gameUserExists(UUID.fromString(id), username)) {
            logger.info("Game user already exists");
            return ResponseEntity.ok("Game user already exists");
        } else {
            GameUserDto gameUserDto = new GameUserDto(username, UUID.fromString(id));
            gameUserService.createGameUser(gameUserDto);
            logger.info("Game user " + username + " created");
            return ResponseEntity.ok("Game user " + username + " created");
        }
    }

    @GetMapping("/userProfile")
    public ResponseEntity<GameUserDto> getGameUser(@RequestParam UUID userId) {
        if (userId == null) {
            logger.warning("Invalid  data");
            return ResponseEntity.badRequest().build();
        }
        GameUserDto gameUserDto = new GameUserDto(gameUserService.getGameUser(userId), gameUserService.getGamesPlayed(userId), gameUserService.getGamesWon(userId));
        logger.info("Game user " + gameUserDto.getUsername() + " found");
        return ResponseEntity.ok(gameUserDto);
    }

    @GetMapping("/achievements/{userId}")
    public ResponseEntity<List<GameUserAchievementDto>> getUserAchievements(@PathVariable UUID userId) {
        List<GameUserAchievement> achievements = gameUserAchievementService.getAchievementsForUser(userId);
        List<GameUserAchievementDto> gameUserAchievementDtos = achievements.stream().map(achievement -> {
            return new GameUserAchievementDto(
                    achievement.getAchievement().getTitle(),
                    achievement.getAchievement().getDescription(),
                    achievement.getDateAchieved()
            );
        }).collect(Collectors.toList());
        return ResponseEntity.ok(gameUserAchievementDtos);
    }

    //Get all game users
    @GetMapping("/users")
    public ResponseEntity<List<GameUserDto>> getGameUsers() {
        if (gameUserService.getGameUsers().isEmpty()) {
            logger.warning("No game users found");
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(gameUserService.getGameUsers());
    }

    //Get all game users with name
    @GetMapping("/users/{username}")
    public ResponseEntity<List<UserFriendDto>> getGameUsers(@PathVariable String username, @RequestParam String uuid) {
        logger.log(INFO, "Getting game users with name {0}", username);
        if (uuid == null) {
            logger.warning("Invalid game user data");
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(gameUserService.getGameUsersWithName(UUID.fromString(uuid), username));
    }

    //Get all friends from single user
    @GetMapping("/friends")
    public ResponseEntity<List<UserFriendDto>> getFriends(@RequestParam UUID userId) {
        if (userId == null) {
            logger.warning("Invalid  data");
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(gameUserService.getFriends(userId));
    }

    //Send friend request
    @PostMapping("/friendRequest/{friendUsername}")
    public ResponseEntity<String> friendRequest(@RequestParam UUID userId, @PathVariable String friendUsername) {
        if (userId == null) {
            logger.warning("Invalid  data");
            return ResponseEntity.badRequest().build();
        }
        if (gameUserService.addFriendRequest(userId, friendUsername)) {
            logger.info("FriendRequest for  " + friendUsername + " added");
            return ResponseEntity.ok("Friend " + friendUsername + " added");
        } else {
            logger.warning("Friend " + friendUsername + " not added");
            return ResponseEntity.badRequest().body("Friend " + friendUsername + " not added");
        }
    }

    //Accept friend request
    @PostMapping("/friendRequest/accept/{requestId}")
    public ResponseEntity<String> friend(@RequestParam UUID userId, @PathVariable String requestId) {
        if (userId == null) {
            logger.warning("Invalid  data");
            return ResponseEntity.badRequest().build();
        }
        if (gameUserService.addFriend(userId, requestId)) {
            logger.info("Friend request " + requestId + " accepted");
            return ResponseEntity.ok("Friend request " + requestId + " accepted");
        } else {
            logger.warning("Something went wrong friendReques " + requestId + " not accepted");
            return ResponseEntity.badRequest().body("Something went wrong friendReques " + requestId + " not accepted");
        }
    }

    //Decline friend request
    @PostMapping("/friendRequest/decline/{requestId}")
    public ResponseEntity<String> declineFriendRequest(@RequestParam UUID userId, @PathVariable String requestId) {
        if (userId == null) {
            logger.warning("Invalid  data");
            return ResponseEntity.badRequest().build();
        }
        if (gameUserService.declineFriendRequest(userId, requestId)) {
            logger.info("FriendRequest " + requestId + " declined");
            return ResponseEntity.ok("FriendRequest " + requestId + " declined");
        } else {
            logger.warning("FriendRequest " + requestId + " not declined");
            return ResponseEntity.badRequest().body("FriendRequest " + requestId + " not declined");
        }
    }

    //Get friend requests from single user
    @GetMapping("/friendRequests")
    public ResponseEntity<List<FriendRequestDto>> friendRequests(@RequestParam UUID userId) {
        if (userId == null) {
            logger.warning("Invalid  data");
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(gameUserService.getFriendRequests(userId));
    }
}