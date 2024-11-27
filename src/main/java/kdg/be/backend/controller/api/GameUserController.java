package kdg.be.backend.controller.api;

import kdg.be.backend.controller.dto.GameUserDTO;
import kdg.be.backend.service.GameUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
        String id = userData.get("id");
        String username = userData.get("username");
        if (id == null || username == null) {
            logger.warning("Invalid game user data");
            return ResponseEntity.badRequest().body("Invalid game user data");
        } else if (gameUserService.gameUserExists(UUID.fromString(id))) {
            logger.info("Game user already exists");
            return ResponseEntity.badRequest().body("Game user already exists");
        } else {
            GameUserDTO gameUserDTO = new GameUserDTO(username, id);
            gameUserService.createGameUser(gameUserDTO.getUsername(), gameUserDTO.getId());
            logger.info("Game user " + username + " created");
            return ResponseEntity.ok("Game user " + username + " created");
        }
    }
}