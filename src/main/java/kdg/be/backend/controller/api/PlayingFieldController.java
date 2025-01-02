package kdg.be.backend.controller.api;

import kdg.be.backend.controller.dto.game.PlayingFieldDto;
import kdg.be.backend.service.PlayingFieldService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static kdg.be.backend.controller.dto.mapper.GameDtoMapper.mapToPlayingFieldDto;

@RestController
@RequestMapping("/api/playingFields")
public class PlayingFieldController {
    private final PlayingFieldService playingFieldService;

    public PlayingFieldController(PlayingFieldService playingFieldService) {
        this.playingFieldService = playingFieldService;
    }

    @GetMapping("/{gameId}")
    public PlayingFieldDto getPlayingFieldByGameId(@PathVariable UUID gameId) {
        return mapToPlayingFieldDto(playingFieldService.getPlayingFieldByGameId(gameId));
    }

}
