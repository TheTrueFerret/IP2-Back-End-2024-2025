package kdg.be.backend.controller.dto.mapper;

import kdg.be.backend.controller.dto.player.PlayerDto;
import kdg.be.backend.controller.dto.player.SimplePlayerDto;
import kdg.be.backend.domain.Player;

import java.util.List;
import java.util.stream.Collectors;

public class PlayerMapper {

    public static List<PlayerDto> mapToPlayerDtos(List<Player> players) {
        return players.stream()
                .map(player -> new PlayerDto(
                        player.getId(),
                        player.getGameUser().getUsername(),
                        player.getScore(),
                        player.getGame().getId(),
                        GameDtoMapper.mapToDeckDto(player.getDeck())
                ))
                .collect(Collectors.toList());
    }

    public static List<SimplePlayerDto> mapToSimplePlayerDtos(List<Player> players) {
        return players.stream()
                .map(player -> new SimplePlayerDto(
                        player.getId(),
                        player.getGameUser().getUsername(),
                        player.getScore(),
                        player.getGame().getId()
                ))
                .collect(Collectors.toList());
    }
}
