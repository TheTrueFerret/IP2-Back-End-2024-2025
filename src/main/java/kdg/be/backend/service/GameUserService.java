package kdg.be.backend.service;

import kdg.be.backend.controller.dto.GameUserDTO;
import kdg.be.backend.domain.GameUser;
import kdg.be.backend.repository.GameUserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GameUserService {

    GameUserRepository gameUserRepository;

    public GameUserService(GameUserRepository gameUserRepository) {
        this.gameUserRepository = gameUserRepository;
    }

    public void createGameUser(GameUserDTO gameUserDTO) {
        GameUser gameUser = new GameUser(gameUserDTO.getUsername(), gameUserDTO.getId());
        gameUserRepository.save(gameUser);
    }

    public boolean gameUserExists(UUID id) {
        return gameUserRepository.existsById(id);
    }
}
