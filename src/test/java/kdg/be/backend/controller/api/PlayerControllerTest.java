package kdg.be.backend.controller.api;

import com.jayway.jsonpath.JsonPath;
import kdg.be.backend.TestContainerIPConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(TestContainerIPConfiguration.class)
@AutoConfigureMockMvc
class PlayerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testGetPlayerScore_ShouldReturnCorrectScore() throws Exception {
        UUID playerId = UUID.fromString("00000000-0000-0000-0000-000000000014");

        MvcResult result = mockMvc.perform(get("/api/players/{playerId}/score", playerId)
                        .contentType(MediaType.ALL))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        System.out.println("Get Player Score Response: " + jsonResponse);
        int score = Integer.parseInt(jsonResponse);
        assertTrue(score >= 0, "The player's score should be a non-negative integer.");
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testGetPlayerScore_ShouldReturnBadRequestForInvalidPlayerId() throws Exception {
        UUID invalidPlayerId = UUID.fromString("00000000-0000-0000-0000-000000000099");

        mockMvc.perform(get("/api/players/{playerId}/score", invalidPlayerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").value("Player not found with ID: 00000000-0000-0000-0000-000000000099"));
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testGetDeckTilesOfPlayer_ShouldReturnTiles() throws Exception {
        UUID playerId = UUID.fromString("00000000-0000-0000-0000-000000000014");

        mockMvc.perform(get("/api/players/tiles/{playerId}", playerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testGetDeckTilesOfPlayer_ShouldReturnBadRequestForInvalidPlayerId() throws Exception {
        UUID invalidPlayerId = UUID.fromString("00000000-0000-0000-0000-000000000099");

        mockMvc.perform(get("/api/players/tiles/{playerId}", invalidPlayerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").value("Couldn't find the deck tiles of player with ID: " + invalidPlayerId));
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testGetCurrentPlayerTurn_ShouldReturnCurrentPlayer() throws Exception {
        // Start the game
        UUID lobbyId = UUID.fromString("ef673b41-d76d-4b96-99d8-41beef0c3707");
        String startGameRequest = """
                {
                  "turnTime": 60,
                  "startTileAmount": 14,
                  "hostUserId": "d61e872f-7784-4e27-996b-cad743916105"
                }
            """;

        MvcResult result = mockMvc.perform(post("/api/games/start/{gameId}", lobbyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(startGameRequest))
                .andExpect(status().isOk())
                .andReturn();

        // Extract the gameId from the response
        String jsonResponse = result.getResponse().getContentAsString();
        UUID gameId = UUID.fromString(JsonPath.parse(jsonResponse).read("$.gameId", String.class));

        // Get the current player turn
        mockMvc.perform(get("/api/players/game/{gameId}/turns/current-player-turn", gameId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testGetCurrentPlayerTurn_ShouldReturnBadRequestForInvalidGameId() throws Exception {
        UUID gameId = UUID.fromString("00000000-0000-0000-0000-000000000099");

        mockMvc.perform(get("/api/players/game/{gameId}/turns/current-player-turn", gameId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").value("Player turn orders not found"));
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testGetPlayersOfGame_ShouldReturnPlayers() throws Exception {
        UUID gameId = UUID.fromString("00000000-0000-0000-0000-000000000011");

        mockMvc.perform(get("/api/players/game/{gameId}", gameId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testGetPlayersOfGame_ShouldReturnBadRequestForInvalidGameId() throws Exception {
        UUID invalidGameId = UUID.fromString("00000000-0000-0000-0000-000000000099");

        mockMvc.perform(get("/api/players/game/{gameId}", invalidGameId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").value("Couldn't find players of game with ID: " + invalidGameId));
    }
}