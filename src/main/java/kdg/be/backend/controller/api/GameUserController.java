package kdg.be.backend.controller.api;

import kdg.be.backend.controller.dto.game.AchievementDto;
import kdg.be.backend.controller.dto.game.GameUserDto;
import kdg.be.backend.domain.GameUserAchievement;
import kdg.be.backend.service.GameUserAchievementService;
import kdg.be.backend.service.GameUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
    public ResponseEntity<List<AchievementDto>> getUserAchievements(@PathVariable UUID userId) {
        List<GameUserAchievement> achievements = gameUserAchievementService.getAchievementsForUser(userId);
        List<AchievementDto> achievementDtos = achievements.stream().map(achievement -> {
            return new AchievementDto(
                    achievement.getAchievement().getTitle(),
                    achievement.getAchievement().getDescription(),
                    achievement.getDateAchieved()
            );
        }).collect(Collectors.toList());
        logger.info("Achievements for user " + userId + " retrieved");
        return ResponseEntity.ok(achievementDtos);
    }
}