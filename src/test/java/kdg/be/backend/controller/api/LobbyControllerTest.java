package kdg.be.backend.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import kdg.be.backend.service.LobbyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LobbyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LobbyService lobbyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testGetLobbyByLobbyIdShouldReturnOk() throws Exception {


        mockMvc.perform(get("/api/lobby/a1e4c8d3-9f3b-4c8e-85ba-7fcf1eb8d006"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.joinCode").exists())
                .andExpect(jsonPath("$.status").exists()).andDo(result -> {
                    String response = result.getResponse().getContentAsString();
                    String prettyResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMapper.readTree(response));
                    System.out.println("Response Body: " + prettyResponse);
                });
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testGetAllLobbiesShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/lobby"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].joinCode").exists())
                .andExpect(jsonPath("$[0].status").exists())
                .andDo(result -> {
                    String response = result.getResponse().getContentAsString();
                    String prettyResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMapper.readTree(response));
                    System.out.println("Response Body: " + prettyResponse);
                });
    }


    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testCreateLobbyShouldReturnCreated() throws Exception {
        String requestBody = """
                    {
                        "joinCode": "TEST",
                        "minimumPlayers": 2,
                        "maximumPlayers": 2
                    }
                """;


        mockMvc.perform(post("/api/lobby/create?userId=1c14c66a-b034-4531-a1e2-dfb07e7f5707")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.joinCode").value("TEST"))
                .andExpect(jsonPath("$.minimumPlayers").value(2))
                .andExpect(jsonPath("$.maximumPlayers").value(2))
                .andDo(result -> {
                    String response = result.getResponse().getContentAsString();
                    String prettyResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMapper.readTree(response));
                    System.out.println("Response Body: " + prettyResponse);
                });
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testJoinLobbyShouldReturnOk() throws Exception {
        String requestBody = """
                    {
                        "joinCode": "JOIN123"
                    }
                """;


        mockMvc.perform(patch("/api/lobby/join/a1e4c8d3-9f3b-4c8e-85ba-7fcf1eb8d006?userId=00000000-0000-0000-0000-000000000008")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.joinCode").value("JOIN123"))
                .andDo(result -> {
                    String response = result.getResponse().getContentAsString();
                    String prettyResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMapper.readTree(response));
                    System.out.println("Response Body: " + prettyResponse);
                });
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testJoinFullLobbyShouldReturnConflict() throws Exception {
        String requestBody = """
                    {
                        "joinCode": "JOINME"
                    }
                """;


        mockMvc.perform(patch("/api/lobby/join/ef673b41-d76d-4b96-99d8-41beef0c3707?userId=00000000-0000-0000-0000-000000000009")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andDo(result -> {
                    String response = result.getResponse().getContentAsString();
                    String prettyResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMapper.readTree(response));
                    System.out.println("Response Body: " + prettyResponse);
                });
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testLeaveLobbyShouldReturnOk() throws Exception {
        mockMvc.perform(patch("/api/lobby/leave/a1e4c8d3-9f3b-4c8e-85ba-7fcf1eb8d006?userId=c4a2fa67-6a4d-4d9b-9c59-4f96b6fbc104")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String response = result.getResponse().getContentAsString();
                    String prettyResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMapper.readTree(response));
                    System.out.println("Response Body: " + prettyResponse);
                });
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testStartLobbyShouldReturnOk() throws Exception {
        mockMvc.perform(patch("/api/lobby/start/a1e4c8d3-9f3b-4c8e-85ba-7fcf1eb8d006")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String response = result.getResponse().getContentAsString();
                    String prettyResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMapper.readTree(response));
                    System.out.println("Response Body: " + prettyResponse);
                });
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testJoinLobbyWithInvalidJoinCodeShouldReturnBadRequest() throws Exception {
        String requestBody = """
                    {
                        "joinCode": "INVALIDCODE"
                    }
                """;


        mockMvc.perform(patch("/api/lobby/join/a1e4c8d3-9f3b-4c8e-85ba-7fcf1eb8d006?userId=00000000-0000-0000-0000-000000000008")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andDo(result -> {
                    String response = result.getResponse().getContentAsString();
                    String prettyResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMapper.readTree(response));
                    System.out.println("Response Body: " + prettyResponse);
                });
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testJoinLobbyWithInvalidUserIdShouldReturnBadRequest() throws Exception {
        String requestBody = """
                    {
                        "joinCode": "JOIN123"
                    }
                """;


        mockMvc.perform(patch("/api/lobby/join/a1e4c8d3-9f3b-4c8e-85ba-7fcf1eb8d006?userId=00000000-0000-0000-0000-000000000999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andDo(result -> {
                    String response = result.getResponse().getContentAsString();
                    String prettyResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMapper.readTree(response));
                    System.out.println("Response Body: " + prettyResponse);
                });
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void testLeaveLobbyWhenUserNotInLobbyShouldReturnBadRequest() throws Exception {
        mockMvc.perform(patch("/api/lobby/leave/a1e4c8d3-9f3b-4c8e-85ba-7fcf1eb8d006?userId=00000000-0000-0000-0000-000000000008")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andDo(result -> {
                    String response = result.getResponse().getContentAsString();
                    String prettyResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMapper.readTree(response));
                    System.out.println("Response Body: " + prettyResponse);
                });
    }
}