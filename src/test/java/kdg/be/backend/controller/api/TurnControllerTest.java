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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(TestContainerIPConfiguration.class)
@AutoConfigureMockMvc
class TurnControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GameService gameService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DirtiesContext
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

        MvcResult startGameResult = mockMvc.perform(post("/api/games/start/41111111-1111-1111-1111-111111111111")
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
                        "numberValue": 10,
                        "color": "BLUE",
                        "gridColumn": 0,
                        "gridRow": 0
                      }
                    ]
                  }
                }
                """.formatted(firstPlayerTurnId);

        mockMvc.perform(post("/api/turns/player-make-move")
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
    @DirtiesContext
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testMakePlayerMove_Initial_ValidMove_ShouldBeOk() throws Exception {
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
        UUID secondPlayerTurnId = UUID.fromString(playerOrder.get(1));


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




        // First move for the second player
        String secondPlayerTurnRequest = """
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
                          "tileId": "00000000-0000-0000-0000-000000000060",
                          "numberValue": 5,
                          "color": "BLUE",
                          "gridColumn": 0,
                          "gridRow": 0
                        },
                        {
                          "tileId": "00000000-0000-0000-0000-000000000061",
                          "numberValue": 5,
                          "color": "BLACK",
                          "gridColumn": 0,
                          "gridRow": 0
                        }
               ]
             }
           }
           """.formatted(gameId, secondPlayerTurnId);


        mockMvc.perform(post("/api/turns/player-make-move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(secondPlayerTurnRequest))
                .andExpect(status().isOk());




        // Second move for the first player
        String secondMoveFirstPlayerRequest = """
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
                   "tileId": "00000000-0000-0000-0000-000000000056",
                   "numberValue": 10,
                   "color": "BLUE",
                   "gridColumn": 0,
                   "gridRow": 0
                 },
                 {
                   "tileId": "00000000-0000-0000-0000-000000000059",
                   "numberValue": 25,
                   "color": "BLACK",
                   "gridColumn": 0,
                   "gridRow": 0
                 }
               ]
             }
           }
           """.formatted(gameId, firstPlayerTurnId);


        mockMvc.perform(post("/api/turns/player-make-move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(secondMoveFirstPlayerRequest))
                .andExpect(status().isOk());


    }


    @Test
    @DirtiesContext
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void givenValidGameAndPlayer_whenPullTile_thenReturnTileAndUpdateDeck_ShouldReturnOk() throws Exception {
        // Stap 1: Start het spel
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
        String playerPullTurnRequest = """
                {
                  "gameId": "%s",
                  "playerId": "%s"
                }
                """.formatted(gameId, firstPlayerTurnId);

        mockMvc.perform(patch("/api/turns/player-pull-tile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(playerPullTurnRequest))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    System.out.println("Pull tile beurtbeheer response: " + jsonResponse);
                });
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void givenValidGameAndPlayerNotInTurn_whenPullTile_thenShouldNotReturnTileAndUpdateDeck_ShouldReturnBadRequest() throws Exception {
        // Stap 1: Start het spel
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
                .andReturn();

        String startGameResponse = startGameResult.getResponse().getContentAsString();
        String gameIdString = JsonPath.parse(startGameResponse).read("$.players[0].gameId", String.class);
        UUID gameId = UUID.fromString(gameIdString);

        List<String> playerOrder = JsonPath.parse(startGameResponse).read("$.playerTurnOrder", List.class);

        // Controleer dat de lijst van playerTurnOrder niet leeg is
        assertFalse(playerOrder.isEmpty(), "Player order should not be empty");

        // Stap 3: Simuleer de beurt van de eerste speler (de eerste in de lijst is die gene die aan het beurt is)
        UUID firstPlayerTurnId = UUID.fromString(playerOrder.getLast());

        // Stap 4: Simuleer een beurt nemen als jij aan het beurt bent
        String playerPullTurnRequest = """
                {
                  "gameId": "%s",
                  "playerId": "%s"
                }
                """.formatted(gameId, firstPlayerTurnId);

        mockMvc.perform(patch("/api/turns/player-pull-tile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(playerPullTurnRequest))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    System.out.println("Pull tile beurtbeheer response: " + jsonResponse);
                });
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testMakeInitialPlayerMove_NotEnoughPoints_ShouldReturnBadRequest() throws Exception {
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
                        "tileId": "00000000-0000-0000-0000-000000000055",
                        "numberValue": 10,
                        "color": "BLUE",
                        "gridColumn": 0,
                        "gridRow": 0
                      },
                      {
                        "tileId": "00000000-0000-0000-0000-000000000056",
                        "numberValue": 10,
                        "color": "BLUE",
                        "gridColumn": 0,
                        "gridRow": 0
                      },
                      {
                        "tileId": "00000000-0000-0000-0000-000000000057",
                        "numberValue": 35,
                        "color": "RED",
                        "gridColumn": 0,
                        "gridRow": 0
                      },
                      {
                        "tileId": "00000000-0000-0000-0000-000000000058",
                        "numberValue": 35,
                        "color": "ORANGE",
                        "gridColumn": 0,
                        "gridRow": 0
                      },
                      {
                        "tileId": "00000000-0000-0000-0000-000000000059",
                        "numberValue": 25,
                        "color": "BLACK",
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
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("You can not place a tileset with a total value of less than 30 in your first move."))
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    System.out.println("Invalid Move Response: " + jsonResponse);
                });
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testMakePlayerMove_WrongTileAttribute_ShouldBeBadRequest() throws Exception {
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
                      "numberValue": 6,
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
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Tile attributes do not match for tile ID: 00000000-0000-0000-0000-000000000062"))
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    String jsonResponsePretty = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMapper.readTree(jsonResponse));
                    System.out.println("Valid Move1 Response: " + jsonResponsePretty);
                });


    }
}