package kdg.be.backend.controller.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
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

import java.util.List;
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

        mockMvc.perform(patch("/api/lobby/ready/ef673b41-d76d-4b96-99d8-41beef0c3707?userId=d61e872f-7784-4e27-996b-cad743916105"))
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(post("/api/game/start/ef673b41-d76d-4b96-99d8-41beef0c3707")
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

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testStartGame_UnhappyPath_LobbyNotStarted() throws Exception {
        String requestBody = """
                {
                    "turnTime": 60,
                    "startTileAmount": 14,
                    "hostUserId": "d61e872f-7784-4e27-996b-cad743916105"
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/game/start/ef673b41-d76d-4b96-99d8-41beef0c3707")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
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

        mockMvc.perform(post("/api/game/start/21111111-1111-1111-1111-111111111111")
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

        mockMvc.perform(post("/api/game/start/{lobbyId}", lobbyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        mockMvc.perform(post("/api/game/start/{lobbyId}", lobbyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testmakePlayerMove_ShouldBeOk() throws Exception {
        // Stap 1: Start het spel
        String startGameRequest = """
                {
                    "turnTime": 60,
                    "startTileAmount": 14,
                    "hostUserId": "11111111-1111-1111-1111-111111111113"
                }
                """;

        MvcResult startGameResult = mockMvc.perform(post("/api/game/start/31111111-1111-1111-1111-111111111111")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(startGameRequest))
                .andExpect(status().isOk())
                .andReturn();


        String startGameResponse = startGameResult.getResponse().getContentAsString();
        String gameIdString = JsonPath.parse(startGameResponse).read("$.players[0].gameId", String.class);
        UUID gameId = UUID.fromString(gameIdString);

        List<String> playerOrder = JsonPath.parse(startGameResponse).read("$.playerTurnOrder", List.class);

        // Controleer dat de lijst van playerTurnOrder niet leeg is
        assertFalse(playerOrder.isEmpty(), "Player order should not be empty");

        // Stap 3: Simuleer de beurt van de eerste speler (de eerste in de lijst is die gene die aan het beurt is)
        UUID firstPlayerTurnId = UUID.fromString(playerOrder.getFirst());


        // Stap 4: Simuleer een beurt nemen als jij aan het beurt bent
        String playerTurnRequest = """
        {
          "gameId": "%s",
          "playerId": "%s",
          "tileSets": [
            {
              "tileSetId": "00000000-0000-0000-0000-000000000002",
              "startCoordinate": 1,
              "endCoordinate": 3,
              "tiles": [
                {
                  "tileId": "00000000-0000-0000-0000-000000000004",
                  "numberValue": 1,
                  "color": "BLUE",
                  "gridColumn": 4,
                  "gridRow": 5
                },
                {
                  "tileId": "00000000-0000-0000-0000-000000000007",
                  "numberValue": 4,
                  "color": "ORANGE",
                  "gridColumn": 7,
                  "gridRow": 10
                }
              ]
            },
            {
              "tileSetId": "00000000-0000-0000-0000-000000000003",
              "startCoordinate": 11,
              "endCoordinate": 13,
              "tiles": [
                {
                  "tileId": "00000000-0000-0000-0000-000000000006",
                  "numberValue": 3,
                  "color": "BLACK",
                  "gridColumn": 7,
                  "gridRow": 8
                },
                {
                  "tileId": "00000000-0000-0000-0000-000000000005",
                  "numberValue": 2,
                  "color": "RED",
                  "gridColumn": 4,
                  "gridRow": 6
                }
              ]
            }
          ],
          "playerDeckDto": {
            "tilesInDeck": [
              {
                "tileId": "00000000-0000-0000-0000-000000000055",
                "numberValue": 5,
                "color": "BLUE",
                "gridColumn": 0,
                "gridRow": 0
              }
            ]
          }
        }
        """.formatted(gameId, firstPlayerTurnId);

        mockMvc.perform(post("/api/game/turn/player-make-move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(playerTurnRequest))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    String jsonResponsePretty = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMapper.readTree(jsonResponse));
                    System.out.println("Beurtbeheer response: " + jsonResponsePretty);
                });
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testManagePlayerMoves_ShouldBeBadRequest() throws Exception {
        // Stap 1: Start het spel
        String startGameRequest = """
                {
                    "turnTime": 60,
                    "startTileAmount": 14,
                    "hostUserId": "11111111-1111-1111-1111-111111111115"
                }
                """;

        MvcResult startGameResult = mockMvc.perform(post("/api/game/start/41111111-1111-1111-1111-111111111111")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(startGameRequest))
                .andExpect(status().isOk())
                .andReturn();


        String startGameResponse = startGameResult.getResponse().getContentAsString();

        List<String> playerOrder = JsonPath.parse(startGameResponse).read("$.playerTurnOrder", List.class);

        // Controleer dat de lijst van playerTurnOrder niet leeg is
        assertFalse(playerOrder.isEmpty(), "Player order should not be empty");

        // Stap 3: Simuleer de beurt van de eerste speler (de eerste in de lijst is die gene die aan het beurt is)
        UUID firstPlayerTurnId = UUID.fromString(playerOrder.getLast());


        // Stap 4: Simuleer een beurt nemen als jij aan het beurt bent
        String playerTurnRequest = """
        {
          "gameId": "0000000-00000-000-0000",
          "playerId": "%s",
          "tileSets": [
            {
              "tileSetId": "00000000-0000-0000-0000-000000000002",
              "startCoordinate": 1,
              "endCoordinate": 3,
              "tiles": [
                {
                  "tileId": "00000000-0000-0000-0000-000000000004",
                  "numberValue": 1,
                  "color": "BLUE",
                  "gridColumn": 4,
                  "gridRow": 5
                },
                {
                  "tileId": "00000000-0000-0000-0000-000000000007",
                  "numberValue": 4,
                  "color": "ORANGE",
                  "gridColumn": 7,
                  "gridRow": 10
                }
              ]
            },
            {
              "tileSetId": "00000000-0000-0000-0000-000000000003",
              "startCoordinate": 11,
              "endCoordinate": 13,
              "tiles": [
                {
                  "tileId": "00000000-0000-0000-0000-000000000006",
                  "numberValue": 3,
                  "color": "BLACK",
                  "gridColumn": 7,
                  "gridRow": 8
                },
                {
                  "tileId": "00000000-0000-0000-0000-000000000005",
                  "numberValue": 2,
                  "color": "RED",
                  "gridColumn": 4,
                  "gridRow": 6
                }
              ]
            }
          ],
          "playerDeckDto": {
            "tilesInDeck": [
              {
                "tileId": "00000000-0000-0000-0000-000000000055",
                "numberValue": 5,
                "color": "BLUE",
                "gridColumn": 0,
                "gridRow": 0
              }
            ]
          }
        }
        """.formatted(firstPlayerTurnId);

        mockMvc.perform(post("/api/game/turn/player-make-move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(playerTurnRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Game not found"))
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    System.out.println("Beurtbeheer response: " + jsonResponse);
                });
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testGetPlayerScore_ShouldReturnCorrectScore() throws Exception {
        UUID playerId = UUID.fromString("00000000-0000-0000-0000-000000000014");

        MvcResult result = mockMvc.perform(get("/api/game/player/{playerId}/score", playerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerId").value(playerId.toString()))
                .andExpect(jsonPath("$.score").isNumber())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        System.out.println("Get Player Score Response: " + jsonResponse);
        int score = JsonPath.parse(jsonResponse).read("$.score", Integer.class);
        assertTrue(score >= 0, "The player's score should be a non-negative integer.");
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testGetPlayerScore_ShouldReturnNotFoundForInvalidPlayerId() throws Exception {
        UUID invalidPlayerId = UUID.fromString("00000000-0000-0000-0000-000000000099");

        mockMvc.perform(get("/api/game/player/{playerId}/score", invalidPlayerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").value("Player not found with ID: 00000000-0000-0000-0000-000000000099"));
    }


}