package kdg.be.backend.controller.dto.mapper;

import kdg.be.backend.controller.dto.GameUserDto;
import kdg.be.backend.domain.GameUser;

public class GameUserDtoMapper {
    public static GameUserDto mapToDto(GameUser gameUser) {
        return new GameUserDto(gameUser.getUsername(), gameUser.getId());
    }
}
