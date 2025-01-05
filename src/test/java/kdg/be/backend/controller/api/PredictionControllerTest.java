package kdg.be.backend.controller.api;

import kdg.be.backend.TestContainerIPConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(TestContainerIPConfiguration.class)
@AutoConfigureMockMvc
class PredictionControllerTest {

    @Autowired
    MockMvc mockMvc;

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    void happyGetAllPredictions() throws Exception {
        mockMvc.perform(get("/api/ai/predictions/{GameName}", "Rummikub"))
                .andExpect(status().isOk())
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    void unHappyGetAllPredictions() throws Exception {
        mockMvc.perform(get("/api/ai/predictions/{GameName}", "GameName"))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "admin", authorities = {"USER"})
    @Test
    void unHappyNoAdminGetAllPredictions() throws Exception {
        mockMvc.perform(get("/api/ai/predictions/{GameName}", "GameName"))
                .andExpect(status().isForbidden());
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    void happyCreatePrediction() throws Exception {
        String requestBody = "{ \"min_players\": \"1\", \"max_players\": \"4\", \"play_time\": \"60\", \"board_game_honor\": \"5\", \"mechanics\": \"strategy\" }";
        mockMvc.perform(post("/api/ai/prediction/{GameName}", "Rummikub")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    void unHappyCreatePrediction() throws Exception {
        String requestBody = "{ \"min_players\": \"1\", \"max_players\": \"4\", \"play_time\": \"60\", \"board_game_honor\": \"5\", \"mechanics\": \"strategy\" }";
        mockMvc.perform(post("/api/ai/prediction/{GameName}", "GameName")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }
}