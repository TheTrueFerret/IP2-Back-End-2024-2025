package kdg.be.backend.controller.dto.prediction;

import lombok.Getter;

@Getter
public class FormDataDto {
    private int min_players;
    private int max_players;
    private int play_time;
    private int board_game_honor;
    private String mechanics;

    public FormDataDto(int min_players, int max_players, int play_time, int board_game_honor, String mechanics) {
        this.min_players = min_players;
        this.max_players = max_players;
        this.play_time = play_time;
        this.board_game_honor = board_game_honor;
        this.mechanics = mechanics;
    }
}
