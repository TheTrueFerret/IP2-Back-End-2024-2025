package kdg.be.backend.controller.api;

import kdg.be.backend.controller.dto.GameUserDTO;
import kdg.be.backend.domain.GameUser;
import kdg.be.backend.service.GameUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.logging.Logger;

@Controller
@RequestMapping("/api/gameuser")
public class GameUserController {

    private final GameUserService gameUserService;
    private final Logger logger = Logger.getLogger(GameUserController.class.getName());

    public GameUserController(GameUserService gameUserService) {
        this.gameUserService = gameUserService;
    }

    @PostMapping("/user")
    public void createGameUser(@RequestBody GameUserDTO gameUserDTO) {
        if (gameUserDTO == null || gameUserDTO.getId() == null || gameUserDTO.getUsername() == null) {
            logger.warning("Invalid game user data");
        } else if (gameUserService.gameUserExists(gameUserDTO.getId())){
            logger.info("Game user already exists");
        } else {
            gameUserService.createGameUser(gameUserDTO);
            logger.info("Game user " + gameUserDTO.getUsername() + " created");
        }

    }

}
