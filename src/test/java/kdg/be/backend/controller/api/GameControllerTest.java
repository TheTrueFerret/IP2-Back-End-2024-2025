package kdg.be.backend.controller.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import kdg.be.backend.TestContainerIPConfiguration;
import kdg.be.backend.service.GameService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestContainerIPConfiguration.class)
@AutoConfigureMockMvc
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GameService gameService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testStartGame_HappyPath() throws Exception {
        String requestBody = """
                {
                    "turnTime": 60,
                    "startTileAmount": 14,
                    "hostUserId": "d61e872f-7784-4e27-996b-cad743916105"
                }
                """;

        //mockMvc.perform(patch("/api/lobby/ready/ef673b41-d76d-4b96-99d8-41beef0c3707?userId=d61e872f-7784-4e27-996b-cad743916105"))
        //        .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(post("/api/games/start/ef673b41-d76d-4b96-99d8-41beef0c3707")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.turnTime").value(60))
                .andExpect(jsonPath("$.startTileAmount").value(14))
                .andExpect(jsonPath("$.dateTime").exists())
                .andExpect(jsonPath("$.playerTurnOrder").isArray())
                .andExpect(jsonPath("$.playerTurnOrder.length()").isNumber())
                .andExpect(jsonPath("$.playingField.tileSetDtos").isArray())
                .andExpect(jsonPath("$.playingField.tileSetDtos.length()").isNumber())
                .andExpect(jsonPath("$.tilePool.tiles").isArray())
                .andExpect(jsonPath("$.tilePool.tiles.length()").isNumber())
                .andExpect(jsonPath("$.players").isArray())
                .andExpect(jsonPath("$.players[0].id").exists())
                .andExpect(jsonPath("$.players[0].username").exists())
                .andExpect(jsonPath("$.players[0].deckDto.deckTilesDto").isArray())
                .andExpect(jsonPath("$.lobby.joinCode").isString())
                .andExpect(jsonPath("$.lobby.minimumPlayers").isNumber())
                .andExpect(jsonPath("$.lobby.maximumPlayers").isNumber())
                .andExpect(jsonPath("$.lobby").exists())
                .andExpect(jsonPath("$.lobby.status").value("READY"))
                .andExpect(jsonPath("$.lobby.hostUser").exists())
                .andExpect(jsonPath("$.lobby.users").isArray())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        if (response.startsWith("{")) {
            String prettyResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMapper.readTree(response));
            System.out.println("Formatted JSON Response: " + prettyResponse);

        } else {
            System.out.println("Plain Text Response: " + response);
            assertTrue(response.contains("Cannot start game"), "Error message should indicate the reason for failure");
        }
    }

//    @Test
//    @WithMockUser(username = "test", password = "test", roles = "USER")
//    void testStartGame_UnhappyPath_LobbyNotStarted() throws Exception {
//        String requestBody = """
//                {
//                    "turnTime": 60,
//                    "startTileAmount": 14,
//                    "hostUserId": "d61e872f-7784-4e27-996b-cad743916105"
//                }
//                """;
//
//        MvcResult result = mockMvc.perform(post("/api/game/start/ef673b41-d76d-4b96-99d8-41beef0c3707")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(requestBody))
//                .andExpect(status().isBadRequest())
//                .andReturn();
//
//        String response = result.getResponse().getContentAsString();
//        if (response.startsWith("{")) {
//            String prettyResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMapper.readTree(response));
//            System.out.println("Formatted JSON Response: " + prettyResponse);
//
//        } else {
//            System.out.println("Plain Text Response: " + response);
//            assertTrue(response.contains("Cannot start game"), "Error message should indicate the reason for failure");
//        }
//    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testGetGameIdByLobbyIdAndUserId_ShouldBeOk() throws Exception {
        UUID lobbyId = UUID.fromString("a1e4c8d3-9f3b-4c8e-85ba-7fcf1eb8d006");
        UUID userId = UUID.fromString("4e861d2e-5f89-47b1-91e4-a3aef9c97b02");

        mockMvc.perform(get("/api/games/lobby/{lobbyId}", lobbyId)
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    System.out.println("Getting GameId Test Response: " + jsonResponse);
                });
    }


    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testStartGame_ValidateTileDistribution_ShouldBeOk() throws Exception {
        String requestBody = """
                {
                    "turnTime": 60,
                    "startTileAmount": 14,
                    "hostUserId": "11111111-1111-1111-1111-111111111111"
                }
                """;

        mockMvc.perform(post("/api/games/start/21111111-1111-1111-1111-111111111111")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.players").isArray())
                .andExpect(jsonPath("$.players[0].deckDto.deckTilesDto.length()").value(14))
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    System.out.println("Tile Distribution Test Response: " + jsonResponse);
                });
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testStartGame_MultipleGamesInSameLobby_ShouldBeBadRequest() throws Exception {
        UUID lobbyId = UUID.fromString("a1e4c8d3-9f3b-4c8e-85ba-7fcf1eb8d006");
        String requestBody = """
                {
                    "turnTime": 60,
                    "startTileAmount": 14
                }
                """;

        mockMvc.perform(post("/api/games/start/{lobbyId}", lobbyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        mockMvc.perform(post("/api/games/start/{lobbyId}", lobbyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }
}