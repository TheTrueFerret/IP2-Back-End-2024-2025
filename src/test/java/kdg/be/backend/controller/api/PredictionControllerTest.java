package kdg.be.backend.controller.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PredictionControllerTest {

    @Autowired
    MockMvc mockMvc;

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    void happyGetPrediction() throws Exception {
        mockMvc.perform(get("/api/ai/prediction/{GameName}", "Rummikub"))
                .andExpect(status().isOk())
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    void unHappyGetPrediction() throws Exception {
        mockMvc.perform(get("/api/ai/prediction/{GameName}", "GameName"))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "user", roles = "USER")
    @Test
    void unHappyNoAdminGetPrediction() throws Exception {
        mockMvc.perform(get("/api/ai/prediction/{GameName}", "Rummikub"))
                .andExpect(status().isForbidden());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    void happyGetAllPredictions() throws Exception {
        mockMvc.perform(get("/api/ai/predictions/{GameName}", "Rummikub"))
                .andExpect(status().isOk())
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    void unHappyGetAllPredictions() throws Exception {
        mockMvc.perform(get("/api/ai/predictions/{GameName}", "GameName"))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    void happyCreatePrediction() throws Exception {
        mockMvc.perform(post("/api/ai/prediction/{GameName}", "Rummikub"))
                .andExpect(status().isOk())
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    void unHappyCreatePrediction() throws Exception {
        mockMvc.perform(post("/api/ai/prediction/{GameName}", "GameName"))
                .andExpect(status().isNotFound());
    }
}