package kdg.be.backend.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import kdg.be.backend.TestContainerIPConfiguration;
import kdg.be.backend.domain.user.GameUserAchievement;
import kdg.be.backend.repository.GameUserAchievementRepository;
import kdg.be.backend.repository.GameUserRepository;
import kdg.be.backend.repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(TestContainerIPConfiguration.class)
@AutoConfigureMockMvc
class GameUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GameUserRepository repo;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameUserAchievementRepository gameUserAchievementRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void createGameUser() throws Exception {
        String body = """
                {
                "id":"a1e4c8d3-9f3b-4c8e-85ba-7fcf1eb8d006",
                "username":"test"
                }
                """;

        mockMvc.perform(post("/api/gameuser/user")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void unHappyCreateGameUser() throws Exception {
        String body = """
                {
                "username":"test"
                }
                """;

        mockMvc.perform(post("/api/gameuser/user")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void getGameUser() throws Exception {
        mockMvc.perform(get("/api/gameuser/userProfile?userId=fbe4a1d1-1c44-49b8-911f-7bc77a78b001")
                        .contentType("application/json"))
                .andExpect(status().isOk()).andDo(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertTrue(content.contains("id"));
                    assertTrue(content.contains("username"));
                });
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void unhappyGetGameUser() throws Exception {
        mockMvc.perform(get("/api/gameuser/userProfile?userId=4e861d2e-1c44-49b8-911f-7bc77a78b001")
                        .contentType("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with id 4e861d2e-1c44-49b8-911f-7bc77a78b001 does not exist."));

    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void happyGetGameUsers() throws Exception {
        mockMvc.perform(get("/api/gameuser/users")
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void happeGetGameUsersByName() throws Exception {
        mockMvc.perform(get("/api/gameuser/users/Player?uuid=fbe4a1d1-1c44-49b8-911f-7bc77a78b001")
                        .contentType("application/json"))
                .andExpect(status().isOk()).andDo(result -> {
                    String content = result.getResponse().getContentAsString();
                    System.out.println("Response content: " + content);
                    assertTrue(content.contains("id"));
                    assertTrue(content.contains("username"));
                });
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void unHappeGetGameUsersByName() throws Exception {
        mockMvc.perform(get("/api/gameuser/users/iffyUh?uuid=fbe4a1d1-1c44-49b8-911f-7bc77a78b001")
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }

    //Get friends of Speler 3 return 1
    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void happyGetOneFriends() throws Exception {
        mockMvc.perform(get("/api/gameuser/friends?userId=4e861d2e-5f89-47b1-91e4-a3aef9c97b02")
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }

    //Get friends of Speler 4 return 0
    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void happyGetNoFriends() throws Exception {
        mockMvc.perform(get("/api/gameuser/friends?userId=fbe4a1d1-1c44-49b8-911f-7bc77a78b001")
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }

    //Get friends of no player return bad request
    //USER DOESN'T EXIST
    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void unHappyGetFriendsWrongUUID() throws Exception {
        mockMvc.perform(get("/api/gameuser/friends?userId=fbe4a1d1-49b8-911f-902f-7bc77a78b001")
                        .contentType("application/json"))
                .andExpect(status().isNotFound());
    }

    //Get friends of no player return bad request
    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void unHappyGetFriends() throws Exception {
        mockMvc.perform(get("/api/gameuser/friends")
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());
    }


    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void happyFriendRequest() throws Exception {
        mockMvc.perform(post("/api/gameuser/friendRequest/Player10?userId=fbe4a1d1-1c44-49b8-911f-7bc77a78b001")
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void unHappyNoUUIDFriendRequest() throws Exception {
        mockMvc.perform(post("/api/gameuser/friendRequest/test")
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void unHappyUserNotFoundFriendRequest() throws Exception {
        mockMvc.perform(post("/api/gameuser/friendRequest/blablablablabla?userId=fbe4a1d1-1c44-49b8-911f-7bc77a78b001")
                        .contentType("application/json"))
                .andExpect(status().isNotFound());
    }

    //Accept friend request from Speler 3 for Speler 4
    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void happyFriend() throws Exception {
        mockMvc.perform(post("/api/gameuser/friendRequest/accept/00000000-0000-0000-0000-000000000001?userId=4e861d2e-5f89-47b1-91e4-a3aef9c97b02")
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }

    //Friend request is not pending
    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void unHappyFriend() throws Exception {
        mockMvc.perform(post("/api/gameuser/friendRequest/accept/00000000-0000-0000-0000-000000000002?userId=87afee3d-2c6b-4876-8f2b-9e1d6f41c503")
                        .contentType("application/json"))
                .andExpect(status().isNotFound());
    }

    //Friend request in database
    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void happyFriendRequests() throws Exception {
        mockMvc.perform(get("/api/gameuser/friendRequests?userId=4e861d2e-5f89-47b1-91e4-a3aef9c97b02")
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }

    //Friend request no uuid given
    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void unHappyNoUUIDFriendRequests() throws Exception {
        mockMvc.perform(get("/api/gameuser/friendRequests")
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());
    }

    //Friend request no friend requests in database
    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void unHappyFriendRequests() throws Exception {
        mockMvc.perform(get("/api/gameuser/friendRequests?userId=1c14c66a-b034-4531-a1e2-dfb07e7f5707")
                        .contentType("application/json"))
                .andExpect(status().isNotFound());
    }
}