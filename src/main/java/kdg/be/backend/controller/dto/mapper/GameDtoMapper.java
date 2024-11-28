package kdg.be.backend.controller.dto.mapper;

import kdg.be.backend.controller.dto.DeckDto;
import kdg.be.backend.controller.dto.GameDto;
import kdg.be.backend.controller.dto.PlayerDto;
import kdg.be.backend.controller.dto.PlayingFieldDto;
import kdg.be.backend.controller.dto.tiles.TileDto;
import kdg.be.backend.controller.dto.tiles.TilePoolDto;
import kdg.be.backend.controller.dto.tiles.TileSetDto;
import kdg.be.backend.domain.*;

import java.util.List;

public class GameDtoMapper {

    public static GameDto mapToGameDto(Game game) {
        PlayingFieldDto playingFieldDto = new PlayingFieldDto(
                game.getPlayingField().getTileSets().stream()
                        .map(GameDtoMapper::mapToTileSetDto)
                        .toList()
        );

        TilePoolDto tilePoolDto = new TilePoolDto(
                game.getTilePool().getTiles().stream()
                        .map(GameDtoMapper::mapToTileDto)
                        .toList()
        );

        List<PlayerDto> playerDtos = game.getPlayers().stream()
                .map(GameDtoMapper::mapToPlayerDto)
                .toList();

        return new GameDto(
                game.getTurnTime(),
                game.getStartTileAmount(),
                playingFieldDto,
                tilePoolDto,
                playerDtos
        );
    }

    private static TileSetDto mapToTileSetDto(TileSet tileSet) {
        return new TileSetDto(
                tileSet.getStartCoordinate(),
                tileSet.getEndCoordinate(),
                tileSet.getTiles().stream()
                        .map(GameDtoMapper::mapToTileDto)
                        .toList()
        );
    }

    private static TileDto mapToTileDto(Tile tile) {
        return new TileDto(tile.getNumberValue(), tile.getTileColor());
    }

    private static PlayerDto mapToPlayerDto(Player player) {
        return new PlayerDto(player.getId(), player.getGameUser().getUsername(), player.getGame().getId(), mapToDeckDto(player.getDeck()));
    }

    public static DeckDto mapToDeckDto(Deck deck) {
        return new DeckDto(deck.getTiles().stream().map(GameDtoMapper::mapToTileDto).toList());
    }
}