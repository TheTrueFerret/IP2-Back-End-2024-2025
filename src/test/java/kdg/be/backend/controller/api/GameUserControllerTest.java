package kdg.be.backend.controller.api;

import kdg.be.backend.TestContainerIPConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(TestContainerIPConfiguration.class)
@AutoConfigureMockMvc
class GameUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
        mockMvc.perform(get("/api/gameuser/userProfile?userId=fbe4a1d1-1c44-49b8-911f-7bc77a78b001")
                        .contentType("application/json"))
                .andExpect(status().isNotFound());
    }


    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void happyGetGameUsers() throws Exception {
        mockMvc.perform(get("/api/gameuser/users")
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }

    //NO USERS IN DATABASE
    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void unHappyGetGameUsers() throws Exception{
        mockMvc.perform(get("/api/gameuser/users")
                        .contentType("application/json"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void happyFriendRequest() throws Exception{
        mockMvc.perform(post("/api/gameuser/friendRequest/test?userId=fbe4a1d1-1c44-49b8-911f-7bc77a78b001")
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void unHappyNoUUIDFriendRequest() throws Exception{
        mockMvc.perform(post("/api/gameuser/friendRequest/test")
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void unHappyUserNotFoundFriendRequest() throws Exception{
        mockMvc.perform(post("/api/gameuser/friendRequest/blablablablabla?userId=fbe4a1d1-1c44-49b8-911f-7bc77a78b001")
                        .contentType("application/json"))
                .andExpect(status().isNotFound());
    }

    //Friend request already exists
    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void happyFriend() throws Exception{
        //Create friend request before accepting
        mockMvc.perform(post("/api/gameuser/friendRequest/test?userId=fbe4a1d1-1c44-49b8-911f-7bc77a78b001")
                        .contentType("application/json"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/gameuser/friend/test?userId=fbe4a1d1-1c44-49b8-911f-7bc77a78b001")
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }

    //Friend request is not pending
    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void unHappyFriend() throws Exception{
    }

    //Friend request in database
    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void happyFriendRequests() throws Exception{
        mockMvc.perform(get("/api/gameuser/friendRequests?userId=fbe4a1d1-1c44-49b8-911f-7bc77a78b001")
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }

    //Freind request no uuid given
    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void unHappyNoUUIDFriendRequests() throws Exception{
        mockMvc.perform(get("/api/gameuser/friendRequests")
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());
    }

    //Freind request no friend requests in database
    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    void unHappyFriendRequests() throws Exception{
        mockMvc.perform(get("/api/gameuser/friendRequests?userId=fbe4a1d1-1c44-49b8-911f-7bc77a78b001")
                        .contentType("application/json"))
                .andExpect(status().isNotFound());
    }
}