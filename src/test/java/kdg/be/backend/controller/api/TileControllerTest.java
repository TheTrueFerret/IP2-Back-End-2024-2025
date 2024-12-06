package kdg.be.backend.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import kdg.be.backend.TestContainerIPConfiguration;
import kdg.be.backend.service.TileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(TestContainerIPConfiguration.class)
@AutoConfigureMockMvc
class TileControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TileService tileService;


    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testGetTilesByTilePoolIdShouldReturnOk() throws Exception {
        UUID tilePoolId = UUID.fromString("00000000-0000-0000-0000-000000000010");

        mockMvc.perform(get("/api/tiles/by-tilepool/" + tilePoolId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andDo(result -> {
                    String response = result.getResponse().getContentAsString();
                    String prettyResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMapper.readTree(response));
                    System.out.println("Formatted JSON Response: " + prettyResponse);
                });
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testGetTilesByTilePoolIdShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/tiles/by-tilepool/" + "invalidId"))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    String response = result.getResponse().getContentAsString();
                    System.out.println("Error Response: " + response);
                });
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testGetTilesByTileSetIdShouldReturnOk() throws Exception {
        UUID tileSetId = UUID.fromString("00000000-0000-0000-0000-000000000002");

        mockMvc.perform(get("/api/tiles/by-tileset/" + tileSetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andDo(result -> {
                    String response = result.getResponse().getContentAsString();
                    String prettyResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMapper.readTree(response));
                    System.out.println("Formatted JSON Response: " + prettyResponse);
                });
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testGetTilesByTileSetIdShouldReturnBadRequest() throws Exception {

        mockMvc.perform(get("/api/tiles/by-tileset/" + "invalidId"))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    String response = result.getResponse().getContentAsString();
                    System.out.println("Error Response: " + response);
                });
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testGetTilesByDeckIdShouldReturnOk() throws Exception {
        UUID deckId = UUID.fromString("00000000-0000-0000-0000-000000000012");

        mockMvc.perform(get("/api/tiles/by-deck/" + deckId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andDo(result -> {
                    String response = result.getResponse().getContentAsString();
                    String prettyResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMapper.readTree(response));
                    System.out.println("Formatted JSON Response: " + prettyResponse);
                });
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testGetTilesByDeckIdShouldReturnBadRequest() throws Exception {

        mockMvc.perform(get("/api/tiles/by-deck/" + "invalidId"))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    String response = result.getResponse().getContentAsString();
                    System.out.println("Error Response: " + response);
                });
    }
}