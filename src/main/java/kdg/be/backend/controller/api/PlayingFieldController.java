package kdg.be.backend.controller.api;

import kdg.be.backend.controller.dto.tiles.TileSetDto;
import kdg.be.backend.service.PlayingFieldService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static kdg.be.backend.controller.dto.mapper.GameDtoMapper.mapToTileSetListDto;


@RestController
@RequestMapping("/api/playingFields")
public class PlayingFieldController {
    private final PlayingFieldService playingFieldService;

    public PlayingFieldController(PlayingFieldService playingFieldService) {
        this.playingFieldService = playingFieldService;
    }

    @GetMapping("/{gameId}")
    public List<TileSetDto> getPlayingFieldByGameId(@PathVariable UUID gameId) {
        return mapToTileSetListDto(playingFieldService.getPlayingFieldByGameId(gameId));
    }
}
