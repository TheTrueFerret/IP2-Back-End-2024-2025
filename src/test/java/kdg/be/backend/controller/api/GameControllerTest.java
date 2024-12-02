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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.hamcrest.Matchers.matchesPattern;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;


import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Import(TestContainerIPConfiguration.class)
@AutoConfigureMockMvc
class GameControllerTest{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GameService gameService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testStartGame_HappyPath() throws Exception {
        String requestBody = """
            {
                "turnTime": 60,
                "startTileAmount": 14
            }
            """;

        mockMvc.perform(patch("/api/lobby/start/a1e4c8d3-9f3b-4c8e-85ba-7fcf1eb8d006"))
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(post("/api/game/start/a1e4c8d3-9f3b-4c8e-85ba-7fcf1eb8d006")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roundTime").value(60))
                .andExpect(jsonPath("$.startTileAmount").value(14))
                .andExpect(jsonPath("$.playingField.tileSetDtos").isArray())
                .andExpect(jsonPath("$.playingField.tileSetDtos.length()").isNumber())
                .andExpect(jsonPath("$.tilePool.tiles").isArray())
                .andExpect(jsonPath("$.tilePool.tiles.length()").isNumber())
                .andExpect(jsonPath("$.players").isArray())
                .andExpect(jsonPath("$.players[0].id").exists())
                .andExpect(jsonPath("$.players[0].username").exists())
                .andExpect(jsonPath("$.players[0].deckDto.deckTilesDto").isArray())
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
    void testStartGame_UnhappyPath_LobbyNotStarted() throws Exception {
        String requestBody = """
            {
                "turnTime": 60,
                "startTileAmount": 14
            }
            """;

        MvcResult result = mockMvc.perform(post("/api/game/start/a1e4c8d3-9f3b-4c8e-85ba-7fcf1eb8d006")
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


}