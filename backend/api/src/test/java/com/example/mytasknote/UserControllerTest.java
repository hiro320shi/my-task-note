package com.example.mytasknote;

import com.example.mytasknote.user.controller.UserController;
import com.example.mytasknote.user.service.UserService;
import com.example.mytasknote.user.repository.UserRepository;
import com.example.mytasknote.user.entity.User;

import com.example.mytasknote.common.exception.DuplicateUsernameException;
import com.example.mytasknote.auth.jwt.JwtAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;      // ← 追加（デバッグ用）
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)   // ★ セキュリティフィルタ無効化
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserService userService;

    @MockBean
    UserRepository userRepository;

    @MockBean
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void register_success_returns201() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser01");

        Mockito.when(userService.register("testuser01", "password123", "テスト太郎"))
                .thenReturn(user);

        var body = """
                {
                  "username": "testuser01",
                  "password": "password123",
                  "displayName": "テスト太郎"
                }
                """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())                         // ← 実際のレスポンスをコンソールに出す
                .andExpect(status().isCreated());
    }

    @Test
    void register_duplicateUsername_returns409() throws Exception {
        Mockito.when(userService.register("dupuser", "password123", "重複テスト"))
                .thenThrow(new DuplicateUsernameException("dupuser"));

        var body = """
                {
                  "username": "dupuser",
                  "password": "password123",
                  "displayName": "重複テスト"
                }
                """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Username already exists"));
    }

    @Test
    void register_validationError_returns400() throws Exception {
        var body = """
                {
                  "username": "",
                  "password": "short",
                  "displayName": ""
                }
                """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }
}
