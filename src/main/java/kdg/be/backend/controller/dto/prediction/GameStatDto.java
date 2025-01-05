package kdg.be.backend.controller.dto.prediction;

import lombok.Getter;


public record GameStatDto(
        int year_published,
        int min_players,
        int max_players,
        int play_time,
        int min_age,
        int board_game_honor,
        String mechanics) {
}
