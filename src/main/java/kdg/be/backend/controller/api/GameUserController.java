package kdg.be.backend.controller.api;

import kdg.be.backend.controller.dto.GameUserDto;
import kdg.be.backend.domain.GameUser;
import kdg.be.backend.exception.UserDoesNotExistException;
import kdg.be.backend.service.GameUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/gameuser")
public class GameUserController {

    private final GameUserService gameUserService;
    private final Logger logger = Logger.getLogger(GameUserController.class.getName());

    public GameUserController(GameUserService gameUserService) {
        this.gameUserService = gameUserService;
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


    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Map<String, String>> handleNullPointerException(NullPointerException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", NullPointerException.class.getSimpleName());
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

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

    @PostMapping("/friend/{friendUsername}")
    public ResponseEntity<String> friend(@RequestParam UUID userId, @PathVariable String friendUsername) {
        if (userId == null) {
            logger.warning("Invalid  data");
            return ResponseEntity.badRequest().build();
        }
        if (gameUserService.addFriend(userId, friendUsername)) {
            logger.info("Friend " + friendUsername + " added");
            return ResponseEntity.ok("Friend " + friendUsername + " added");
        } else {
            logger.warning("Friend " + friendUsername + " not added");
            return ResponseEntity.badRequest().body("Friend " + friendUsername + " not added");
        }
    }

    @GetMapping("/friendRequests")
    public ResponseEntity<String> friendRequests(@RequestParam UUID userId) {
        if (userId == null) {
            logger.warning("Invalid  data");
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(gameUserService.getFriendRequests(userId));
    }
}