package kdg.be.backend.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import kdg.be.backend.TestContainerIPConfiguration;
import kdg.be.backend.service.PlayingFieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;

import java.util.UUID;

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
    void getPlayingFieldByGameIdShouldReturnOk() throws Exception {
        UUID gameId = UUID.fromString("00000000-0000-0000-0000-000000000011");

        mockMvc.perform(get("/api/playingFields/" + gameId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tileSetDtos").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    System.out.println("Response: " + jsonResponse);
                });
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void getPlayingFieldByGameIdShouldReturnNotFound() throws Exception {
        UUID gameId = UUID.fromString("00000000-0000-0000-0000-000000000002");

        mockMvc.perform(get("/api/playingFields/" + gameId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("No game found"))
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    System.out.println("Response: " + jsonResponse);
                });
    }
}