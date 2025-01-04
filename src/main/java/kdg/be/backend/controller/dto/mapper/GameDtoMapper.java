package kdg.be.backend.controller.dto.mapper;

import kdg.be.backend.controller.dto.game.*;
import kdg.be.backend.controller.dto.player.PlayerDto;
import kdg.be.backend.controller.dto.tiles.TileDto;
import kdg.be.backend.controller.dto.tiles.TilePoolDto;
import kdg.be.backend.controller.dto.tiles.TileSetDto;
import kdg.be.backend.controller.dto.user.GameUserDto;
import kdg.be.backend.domain.*;
import kdg.be.backend.domain.user.GameUser;

import java.util.ArrayList;
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
                game.getId(),
                game.getTurnTime(),
                game.getStartTileAmount(),
                game.getDateTime(),
                game.getPlayerTurnOrder(),
                playingFieldDto,
                tilePoolDto,
                playerDtos,
                mapToLobbyDto(game.getLobby())
        );
    }

    public static TileSetDto mapToTileSetDto(TileSet tileSet) {
        return new TileSetDto(
                tileSet.getId(),
                tileSet.getStartCoordinate(),
                tileSet.getEndCoordinate(),
                tileSet.getGridRow(),
                tileSet.getTiles().stream()
                        .map(GameDtoMapper::mapToTileDto)
                        .toList()
        );
    }

    public static LobbyDto mapToLobbyDto(Lobby lobby) {
        return new LobbyDto(lobby.getId(), lobby.getStatus(), mapToGameUser(lobby.getHostUser()), lobby.getUsers().stream().map(GameDtoMapper::mapToGameUser).toList(), lobby.getJoinCode(), lobby.getMinimumPlayers(), lobby.getMaximumPlayers());
    }

    public static GameUserDto mapToGameUser(GameUser gameUser) {
        return new GameUserDto(gameUser.getUsername(), gameUser.getId());
    }

    public static TileDto mapToTileDto(Tile tile) {
        return new TileDto(tile.getId(), tile.getNumberValue(), tile.getTileColor(), tile.getGridColumn(), tile.getGridRow());
    }

    public static PlayerDto mapToPlayerDto(Player player) {
        return new PlayerDto(player.getId(), player.getGameUser().getUsername(), player.getScore(), player.getGame().getId(), mapToDeckDto(player.getDeck()));
    }

    public static DeckDto mapToDeckDto(Deck deck) {
        return new DeckDto(deck.getTiles().stream().map(GameDtoMapper::mapToTileDto).toList());
    }

    public static PlayingFieldDto mapToPlayingFieldDto(PlayingField playingField) {
        List<TileSetDto> tileSetDtos = playingField.getTileSets()
                .stream()
                .map(GameDtoMapper::mapToTileSetDto)
                .toList();

        return new PlayingFieldDto(tileSetDtos);
    }

    public static TilePoolDto mapToTilePoolDto(TilePool tilePool) {
        return new TilePoolDto(
                tilePool.getTiles().stream()
                        .map(GameDtoMapper::mapToTileDto)
                        .toList()
        );
    }

    public static List<TileSetDto> mapToTileSetListDto(List<TileSet> tileSets) {
        return tileSets
                .stream()
                .map(GameDtoMapper::mapToTileSetDto)
                .toList();
    }
}