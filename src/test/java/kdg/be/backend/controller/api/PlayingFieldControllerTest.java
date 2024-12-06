package kdg.be.backend.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import kdg.be.backend.TestContainerIPConfiguration;
import kdg.be.backend.controller.dto.AddTileToTilesetRequestDTO;
import kdg.be.backend.service.PlayingFieldService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Import(TestContainerIPConfiguration.class)
@AutoConfigureMockMvc
class PlayingFieldControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlayingFieldService playingFieldService;

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testAddTileToTileSetShouldReturnOk() throws Exception {
        UUID playingFieldId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID tileSetId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        UUID tileId = UUID.fromString("00000000-0000-0000-0000-000000000006");

        // Create a request DTO
        AddTileToTilesetRequestDTO requestDTO = new AddTileToTilesetRequestDTO(playingFieldId, tileSetId, tileId);

        // Serialize the request DTO
        String requestJson = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(post("/api/playing-fields/add-tile")
                        .contentType("application/json")
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tiles").isArray())
                .andExpect(jsonPath("$.tiles").isNotEmpty())
                .andDo(result -> {
                    String response = result.getResponse().getContentAsString();
                    String prettyResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMapper.readTree(response));
                    System.out.println("Formatted JSON Response: " + prettyResponse);
                });
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testAddTileToTileSetShouldReturnBadRequestForInvalidInput() throws Exception {
        // Invalid UUIDs in the request DTO
        AddTileToTilesetRequestDTO invalidRequestDTO = new AddTileToTilesetRequestDTO(null, null, null);

        // Serialize the invalid request DTO
        String invalidRequestJson = objectMapper.writeValueAsString(invalidRequestDTO);

        mockMvc.perform(post("/api/playing-fields/add-tile")
                        .contentType("application/json")
                        .content(invalidRequestJson))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    String response = result.getResponse().getContentAsString();
                    String prettyResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMapper.readTree(response));
                    System.out.println("Formatted JSON Response: " + prettyResponse);
                });
    }
}