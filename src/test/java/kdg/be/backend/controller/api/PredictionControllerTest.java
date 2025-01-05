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
}