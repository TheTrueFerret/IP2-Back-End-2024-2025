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

    @Autowired
    private GameService gameService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testGetPlayerScore_ShouldReturnCorrectScore() throws Exception {
        UUID playerId = UUID.fromString("00000000-0000-0000-0000-000000000014");

        MvcResult result = mockMvc.perform(get("/api/players/{playerId}/score", playerId)
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

        mockMvc.perform(get("/api/players/{playerId}/score", invalidPlayerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").value("Player not found with ID: 00000000-0000-0000-0000-000000000099"));
    }
}