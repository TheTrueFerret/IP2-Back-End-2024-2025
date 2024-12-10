package kdg.be.backend.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import kdg.be.backend.TestContainerIPConfiguration;
import kdg.be.backend.controller.dto.requests.CreateTilesetRequest;
import kdg.be.backend.service.TileSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestContainerIPConfiguration.class)
@AutoConfigureMockMvc
class TilesetControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TileSetService tileSetService;


    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testGetTilesetsByPlayingFieldShouldReturnOk() throws Exception {
        UUID playingFieldId = UUID.fromString("00000000-0000-0000-0000-000000000001");

        mockMvc.perform(get("/api/tilesets/playingfield/" + playingFieldId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andDo(result -> {
                    String response = result.getResponse().getContentAsString();
                    String prettyResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMapper.readTree(response));
                    System.out.println("Formatted JSON Response: " + prettyResponse);
                });;
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testGetTilesetsByPlayingFieldShouldReturnBadRequest() throws Exception {
        UUID playingFieldId = UUID.fromString("00000000-0000-0000-0000-000000000002");

        mockMvc.perform(get("/api/tilesets/playingfield/" + playingFieldId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("No TileSets found for Playing Field: " + playingFieldId))
                .andDo(result -> {
                    String response = result.getResponse().getContentAsString();
                    String prettyResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMapper.readTree(response));
                    System.out.println("Formatted JSON Response: " + prettyResponse);
                });;
    }
}