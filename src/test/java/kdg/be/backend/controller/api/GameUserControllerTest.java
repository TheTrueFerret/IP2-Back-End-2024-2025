package kdg.be.backend.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import kdg.be.backend.TestContainerIPConfiguration;
import kdg.be.backend.domain.user.GameUserAchievement;
import kdg.be.backend.repository.GameUserAchievementRepository;
import kdg.be.backend.repository.PlayerRepository;
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

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(TestContainerIPConfiguration.class)
@AutoConfigureMockMvc
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

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
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
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with id 4e861d2e-1c44-49b8-911f-7bc77a78b001 does not exist."));

    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void happyGetGameUsers() throws Exception {
        mockMvc.perform(get("/api/gameuser/users")
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void happeGetGameUsersByName() throws Exception {
        mockMvc.perform(get("/api/gameuser/users/Player?uuid=fbe4a1d1-1c44-49b8-911f-7bc77a78b001")
                        .contentType("application/json"))
                .andExpect(status().isOk()).andDo(result -> {
                    String content = result.getResponse().getContentAsString();
                    System.out.println("Response content: " + content);
                    assertTrue(content.contains("id"));
                    assertTrue(content.contains("username"));
                });
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void unHappeGetGameUsersByName() throws Exception {
        mockMvc.perform(get("/api/gameuser/users/iffyUh?uuid=fbe4a1d1-1c44-49b8-911f-7bc77a78b001")
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }

    //Get friends of Speler 3 return 1
    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void happyGetOneFriends() throws Exception {
        mockMvc.perform(get("/api/gameuser/friends?userId=4e861d2e-5f89-47b1-91e4-a3aef9c97b02")
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }

    //Get friends of Speler 4 return 0
    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void happyGetNoFriends() throws Exception {
        mockMvc.perform(get("/api/gameuser/friends?userId=fbe4a1d1-1c44-49b8-911f-7bc77a78b001")
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }

    //Get friends of no player return bad request
    //USER DOESN'T EXIST
    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void unHappyGetFriendsWrongUUID() throws Exception {
        mockMvc.perform(get("/api/gameuser/friends?userId=fbe4a1d1-49b8-911f-902f-7bc77a78b001")
                        .contentType("application/json"))
                .andExpect(status().isNotFound());
    }

    //Get friends of no player return bad request
    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void unHappyGetFriends() throws Exception {
        mockMvc.perform(get("/api/gameuser/friends")
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());
    }


    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void happyFriendRequest() throws Exception {
        mockMvc.perform(post("/api/gameuser/friendRequest/Player10?userId=fbe4a1d1-1c44-49b8-911f-7bc77a78b001")
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void unHappyNoUUIDFriendRequest() throws Exception {
        mockMvc.perform(post("/api/gameuser/friendRequest/test")
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void unHappyUserNotFoundFriendRequest() throws Exception {
        mockMvc.perform(post("/api/gameuser/friendRequest/blablablablabla?userId=fbe4a1d1-1c44-49b8-911f-7bc77a78b001")
                        .contentType("application/json"))
                .andExpect(status().isNotFound());
    }

    //Accept friend request from Speler 3 for Speler 4
    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void happyFriend() throws Exception {
        mockMvc.perform(post("/api/gameuser/friendRequest/accept/00000000-0000-0000-0000-000000000001?userId=4e861d2e-5f89-47b1-91e4-a3aef9c97b02")
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }

    //Friend request is not pending
    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void unHappyFriend() throws Exception {
        mockMvc.perform(post("/api/gameuser/friendRequest/accept/00000000-0000-0000-0000-000000000002?userId=87afee3d-2c6b-4876-8f2b-9e1d6f41c503")
                        .contentType("application/json"))
                .andExpect(status().isNotFound());
    }

    //Friend request in database
    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void happyFriendRequests() throws Exception {
        mockMvc.perform(get("/api/gameuser/friendRequests?userId=4e861d2e-5f89-47b1-91e4-a3aef9c97b02")
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }

    //Friend request no uuid given
    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void unHappyNoUUIDFriendRequests() throws Exception {
        mockMvc.perform(get("/api/gameuser/friendRequests")
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());
    }

    //Friend request no friend requests in database
    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void happyNoFriendRequests() throws Exception {
        mockMvc.perform(get("/api/gameuser/friendRequests?userId=1c14c66a-b034-4531-a1e2-dfb07e7f5707")
                        .contentType("application/json"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DirtiesContext
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
                            "tileId": "00000000-0000-0000-0000-000000000063",
                            "numberValue": 5,
                            "color": "RED",
                            "gridColumn": 1,
                            "gridRow": 4
                          },
                          {
                            "tileId": "00000000-0000-0000-0000-000000000064",
                            "numberValue": 6,
                            "color": "RED",
                            "gridColumn": 2,
                            "gridRow": 4
                          },
                          {
                            "tileId": "00000000-0000-0000-0000-000000000065",
                            "numberValue": 7,
                            "color": "RED",
                            "gridColumn": 3,
                            "gridRow": 4
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
    @DirtiesContext
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
                            "tileId": "00000000-0000-0000-0000-000000000063",
                            "numberValue": 5,
                            "color": "RED",
                            "gridColumn": 1,
                            "gridRow": 4
                          },
                          {
                            "tileId": "00000000-0000-0000-0000-000000000064",
                            "numberValue": 6,
                            "color": "RED",
                            "gridColumn": 2,
                            "gridRow": 4
                          },
                          {
                            "tileId": "00000000-0000-0000-0000-000000000065",
                            "numberValue": 7,
                            "color": "RED",
                            "gridColumn": 3,
                            "gridRow": 4
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
                .anyMatch(achievement -> "First Move".equals(achievement.getAchievement().getTitle()));

        assertTrue(hasParticipationAward, "GameUser should have received the 'Participation' award after playing a game");
    }


    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    public void happyGetUserCustomizables() throws Exception {
        mockMvc.perform(get("/api/gameuser/userCustomizables?userId=4e861d2e-5f89-47b1-91e4-a3aef9c97b02")
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    public void unHappyGetUserCustomizables() throws Exception {
        mockMvc.perform(get("/api/gameuser/userCustomizables?userId=4e861d2e-5f89-47b1-91e4-a3aef9000000")
                        .contentType("application/json"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    public void happyGetCustomizables() throws Exception {
        mockMvc.perform(get("/api/gameuser/customizables?userId=4e861d2e-5f89-47b1-91e4-a3aef9000000")
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }
}