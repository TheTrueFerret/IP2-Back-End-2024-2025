package kdg.be.backend.controller.api;

import kdg.be.backend.controller.dto.GameUserDto;
import kdg.be.backend.domain.GameUser;
import kdg.be.backend.service.GameUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            return ResponseEntity.badRequest().body("Game user already exists");
        } else {
            GameUserDto gameUserDto = new GameUserDto(username, UUID.fromString(id));
            gameUserService.createGameUser(gameUserDto);
            logger.info("Game user " + username + " created");
            return ResponseEntity.ok("Game user " + username + " created");
        }
    }

    @GetMapping("/userProfile")
    public ResponseEntity<GameUserDto> getGameUser(@RequestBody Map<String, String> data) {
        UUID id = data.get("id") != null ? UUID.fromString(data.get("id")) : null;
        if (id == null) {
            logger.warning("Invalid  data");
            return ResponseEntity.badRequest().build();
        }
        GameUser gameUser = gameUserService.getGameUser(id);
        if (gameUser != null) {
            GameUserDto gameUserDto = new GameUserDto(gameUserService.getGameUser(id));
            logger.info("Game user " + gameUserDto.getUsername() + " found");
            return ResponseEntity.ok(gameUserDto);
        } else {
            logger.warning("Game user not found by id.");
            return ResponseEntity.notFound().build();
        }
    }
}