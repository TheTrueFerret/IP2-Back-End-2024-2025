package kdg.be.backend.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import kdg.be.backend.TestContainerIPConfiguration;
import kdg.be.backend.controller.dto.requests.AssignTileRequestDTO;
import kdg.be.backend.domain.Game;
import kdg.be.backend.domain.Tile;
import kdg.be.backend.domain.TilePosition;
import kdg.be.backend.domain.enums.TileColor;
import kdg.be.backend.service.GameService;
import kdg.be.backend.service.TilePositionService;
import kdg.be.backend.service.TileService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestContainerIPConfiguration.class)
@AutoConfigureMockMvc
class TilePositionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TilePositionService tilePositionService;

    @Autowired
    private GameService gameService;

    @Autowired
    private TileService tileService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testGetTilePositionsForGame_HappyPath() throws Exception {

        UUID tileId1 = UUID.fromString("00000000-0000-0000-0000-000000000004");
        UUID tileId2 = UUID.fromString("00000000-0000-0000-0000-000000000005");
        UUID tileId3 = UUID.fromString("00000000-0000-0000-0000-000000000006");
        UUID tileId4 = UUID.fromString("00000000-0000-0000-0000-000000000007");


        MvcResult result = mockMvc.perform(get("/api/tile-positions/game/" + "00000000-0000-0000-0000-000000000011"))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();

        assertTrue(jsonResponse.contains(tileId1.toString()));
        assertTrue(jsonResponse.contains(tileId2.toString()));
        assertTrue(jsonResponse.contains(tileId3.toString()));
        assertTrue(jsonResponse.contains(tileId4.toString()));
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testGetTilePositionsForGame_UnhappyPath_InvalidGameId() throws Exception {
        mockMvc.perform(get("/api/tile-positions/game/" + "33333333-3333-3333-3333-333333333333"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Game not found"));
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testAssignTileToPosition_HappyPath() throws Exception {

        AssignTileRequestDTO requestDTO = new AssignTileRequestDTO(UUID.fromString("00000000-0000-0000-0000-000000000011"), UUID.fromString("00000000-0000-0000-0000-000000000004"), 4, 5);

        // Mock behavior for assigning tile to position
        MvcResult result = mockMvc.perform(post("/api/tile-positions/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rowPosition").value(4))
                .andExpect(jsonPath("$.columnPosition").value(5))
                .andExpect(jsonPath("$.tileId").value("00000000-0000-0000-0000-000000000004"))
                .andExpect(jsonPath("$.tileColor").value("BLUE"))
                .andExpect(jsonPath("$.numberValue").value(1)).andReturn();
        String jsonResponse = result.getResponse().getContentAsString();
        System.out.println(jsonResponse);

    }


    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testAssignTileToPosition_UnhappyPath_InvalidTileId() throws Exception {
        // Arrange
        UUID invalidTileId = UUID.fromString("44444444-4444-4444-4444-444444444444");
        AssignTileRequestDTO requestDTO = new AssignTileRequestDTO(UUID.fromString("00000000-0000-0000-0000-000000000011"), invalidTileId, 4, 5);

        // Mock behavior for invalid tile ID
        mockMvc.perform(post("/api/tile-positions/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Tile not found"));
    }
}