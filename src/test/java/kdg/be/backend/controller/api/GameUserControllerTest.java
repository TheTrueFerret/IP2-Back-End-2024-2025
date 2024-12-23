package kdg.be.backend.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import kdg.be.backend.TestContainerIPConfiguration;
import kdg.be.backend.domain.GameUserAchievement;
import kdg.be.backend.repository.GameUserAchievementRepository;
import kdg.be.backend.repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(TestContainerIPConfiguration.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GameUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameUserAchievementRepository gameUserAchievementRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void createGameUser() throws Exception {
        String body = """
                {
                "id":"a1e4c8d3-9f3b-4c8e-85ba-7fcf1eb8d006",
                "username":"test"
                }
                """;

        mockMvc.perform(post("/api/gameuser/user")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void unHappyCreateGameUser() throws Exception {
        String body = """
                {
                "username":"test"
                }
                """;

        mockMvc.perform(post("/api/gameuser/user")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void getGameUser() throws Exception {
        mockMvc.perform(get("/api/gameuser/userProfile?userId=fbe4a1d1-1c44-49b8-911f-7bc77a78b001")
                        .contentType("application/json"))
                .andExpect(status().isOk()).andDo(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertTrue(content.contains("id"));
                    assertTrue(content.contains("username"));
                });
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void unhappyGetGameUser() throws Exception {
        mockMvc.perform(get("/api/gameuser/userProfile?userId=4e861d2e-1c44-49b8-911f-7bc77a78b001")
                        .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User with id 4e861d2e-1c44-49b8-911f-7bc77a78b001 does not exist."));

    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testGameUserReceivesAwardsAfterMove() throws Exception {
        String startGameRequest = """
                {
                    "turnTime": 60,
                    "startTileAmount": 14,
                    "hostUserId": "11111111-1111-1111-1111-111111111113"
                }
                """;

        MvcResult startGameResult = mockMvc.perform(post("/api/games/start/31111111-1111-1111-1111-111111111111")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(startGameRequest))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    String jsonResponsePretty = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMapper.readTree(jsonResponse));
                    System.out.println("Start game Response: " + jsonResponsePretty);
                })
                .andReturn();

        String startGameResponse = startGameResult.getResponse().getContentAsString();
        String gameIdString = JsonPath.parse(startGameResponse).read("$.players[0].gameId", String.class);
        UUID gameId = UUID.fromString(gameIdString);

        List<String> playerOrder = JsonPath.parse(startGameResponse).read("$.playerTurnOrder", List.class);
        UUID firstPlayerTurnId = UUID.fromString(playerOrder.get(0));

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
                          "tileId": "00000000-0000-0000-0000-000000000062",
                          "numberValue": 5,
                          "color": "ORANGE",
                          "gridColumn": 0,
                          "gridRow": 0
                        },
                        {
                          "tileId": "00000000-0000-0000-0000-000000000063",
                          "numberValue": 5,
                          "color": "RED",
                          "gridColumn": 0,
                          "gridRow": 0
                        }
                       ]
                     }
                }
                """.formatted(gameId, firstPlayerTurnId);

        mockMvc.perform(post("/api/turns/player-make-move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(playerTurnRequest))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    String jsonResponsePretty = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMapper.readTree(jsonResponse));
                    System.out.println("Valid Move1 Response: " + jsonResponsePretty);
                });

        UUID gameUserId = playerRepository.findById(firstPlayerTurnId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found"))
                .getGameUser()
                .getId();

        // Check if the GameUser has received the award
        List<GameUserAchievement> achievements = gameUserAchievementRepository.findByGameUser_Id(gameUserId);
        achievements.forEach(achievement -> System.out.println("Achievement Title: " + achievement.getAchievement().getTitle()));

        boolean hasFirstMoveAchievement = achievements.stream()
                .anyMatch(achievement -> "First Move".equals(achievement.getAchievement().getTitle()));

        assertTrue(hasFirstMoveAchievement, "GameUser should have received the 'First Move' achievement after making a move");
    }


    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testGameUserReceivesParticipationAward() throws Exception {
        // Step 1: Start the game
        String startGameRequest = """
            {
                "turnTime": 60,
                "startTileAmount": 14,
                "hostUserId": "11111111-1111-1111-1111-111111111113"
            }
            """;

        MvcResult startGameResult = mockMvc.perform(post("/api/games/start/31111111-1111-1111-1111-111111111111")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(startGameRequest))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    String jsonResponsePretty = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMapper.readTree(jsonResponse));
                    System.out.println("Start game Response: " + jsonResponsePretty);
                })
                .andReturn();

        String startGameResponse = startGameResult.getResponse().getContentAsString();
        String gameIdString = JsonPath.parse(startGameResponse).read("$.players[0].gameId", String.class);
        UUID gameId = UUID.fromString(gameIdString);

        List<String> playerOrder = JsonPath.parse(startGameResponse).read("$.playerTurnOrder", List.class);
        UUID firstPlayerTurnId = UUID.fromString(playerOrder.get(0));

        // Step 2: Simulate the turn of the first player
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
                          "tileId": "00000000-0000-0000-0000-000000000062",
                          "numberValue": 5,
                          "color": "ORANGE",
                          "gridColumn": 0,
                          "gridRow": 0
                        },
                        {
                          "tileId": "00000000-0000-0000-0000-000000000063",
                          "numberValue": 5,
                          "color": "RED",
                          "gridColumn": 0,
                          "gridRow": 0
                        }
                   ]
                 }
            }
            """.formatted(gameId, firstPlayerTurnId);

        mockMvc.perform(post("/api/turns/player-make-move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(playerTurnRequest))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    String jsonResponsePretty = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMapper.readTree(jsonResponse));
                    System.out.println("Valid Move1 Response: " + jsonResponsePretty);
                });

        UUID gameUserId = playerRepository.findById(firstPlayerTurnId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found"))
                .getGameUser()
                .getId();

        // Check if the GameUser has received the "Participation" award
        List<GameUserAchievement> achievements = gameUserAchievementRepository.findByGameUser_Id(gameUserId);
        achievements.forEach(achievement -> System.out.println("Achievement Title: " + achievement.getAchievement().getTitle()));

        boolean hasParticipationAward = achievements.stream()
                .anyMatch(achievement -> "Participation".equals(achievement.getAchievement().getTitle()));

        assertTrue(hasParticipationAward, "GameUser should have received the 'Participation' award after playing a game");
    }

}