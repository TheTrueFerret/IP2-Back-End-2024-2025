package kdg.be.backend.controller.api;

import kdg.be.backend.service.GameUserService;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GameUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createGameUser() throws Exception {
        String body = """
                {
                "id":"a1e4c8d3-9f3b-4c8e-85ba-7fcf1eb8d006",
                "username":"test"
                }
                """;

        String jwtToken = getToken();

        mockMvc.perform(post("/api/gameuser/user")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    void unHappyCreateGameUser() throws Exception {
        String body = """
                {
                "username":"test"
                }
                """;

        String jwtToken = getToken();

        mockMvc.perform(post("/api/gameuser/user")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getGameUser() throws Exception {
        String body = """
                {
                "id":"fbe4a1d1-1c44-49b8-911f-7bc77a78b001"
                }
                """;

        String jwtToken = getToken();

        mockMvc.perform(get("/api/gameuser/userProfile")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    void unhappyGetGameUser() throws Exception {
        String body = """
                {
                "id":"00000000-0000-0000-0000-000000000123"
                }
                """;

        String jwtToken = getToken();

        mockMvc.perform(get("/api/gameuser/userProfile")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isNotFound());
    }


    private String getToken() throws JSONException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);

        String body = "client_id=RummikubApp&client_secret=nNhIIzSj3s8IYRIZouVkGEuQJHQpuGUG&username=test&password=test&grant_type=password&scope=openid";
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:8180/realms/Rummikub/protocol/openid-connect/token",
                HttpMethod.POST,
                request,
                String.class
        );

        JSONObject jsonObject = new JSONObject(response.getBody());
        return jsonObject.getString("access_token");
    }
}