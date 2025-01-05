package kdg.be.backend.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import kdg.be.backend.TestContainerIPConfiguration;
import kdg.be.backend.service.GameService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestContainerIPConfiguration.class)
@AutoConfigureMockMvc
public class AchievementControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void getAllAchievements_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/achievements")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}