package com.example.mytasknote.user.controller;

import com.example.mytasknote.auth.jwt.JwtAuthenticationFilter;
import com.example.mytasknote.common.exception.DuplicateUsernameException;
import com.example.mytasknote.user.entity.User;
import com.example.mytasknote.user.repository.UserRepository;
import com.example.mytasknote.user.service.UserService;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    // あなたのUserControllerのURLに合わせて変えてOK
    // 例：@RequestMapping("/users") なら "/users"
    private static final String POST_URL = "/api/users";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    // UserControllerが直接DIしている場合があるのでモックしておく
    @MockBean
    UserRepository userRepository;

    // SecurityConfigが要求する場合があるのでモック
    @MockBean
    JwtAuthenticationFilter jwtAuthenticationFilter;

    private static String repeat(char c, int n) {
        return String.valueOf(c).repeat(n);
    }

    // ----------------------------
    // 基本（正常系/異常系）
    // ----------------------------

    @Test
    void register_returns2xx_whenValid() throws Exception {
        User saved = new User();
        saved.setId(1L);
        saved.setUsername("user01");
        saved.setDisplayName("表示名");
        saved.setPassword("hashed");

        Mockito.when(userService.register(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString()
        )).thenReturn(saved);

        var body = """
                {
                  "username": "user01",
                  "password": "password123",
                  "displayName": "表示名"
                }
                """;

        mockMvc.perform(post(POST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void register_returns409_whenDuplicateUsername() throws Exception {
        Mockito.when(userService.register(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString()
        )).thenThrow(new DuplicateUsernameException("dupuser"));

        var body = """
                {
                  "username": "dupuser",
                  "password": "password123",
                  "displayName": "表示名"
                }
                """;

        mockMvc.perform(post(POST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_USERNAME"))
                .andExpect(jsonPath("$.message").value("Username already exists"));
    }

    @Test
    void register_returns400_whenValidationError_blankFields() throws Exception {
        var body = """
                {
                  "username": "",
                  "password": "",
                  "displayName": ""
                }
                """;

        mockMvc.perform(post(POST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.username").exists())
                .andExpect(jsonPath("$.errors.password").exists())
                .andExpect(jsonPath("$.errors.displayName").exists());
    }

    // ----------------------------
    // 境界値（username/password/displayName）
    // DTO制約:
    // username: @NotBlank @Size(max=100)
    // password: @NotBlank @Size(min=8, max=100)
    // displayName: @NotBlank @Size(max=100)
    // ----------------------------

    @Test
    void register_returns2xx_whenUsernameIs100() throws Exception {
        String username100 = repeat('u', 100);

        User saved = new User();
        saved.setId(1L);
        saved.setUsername(username100);
        saved.setDisplayName("表示名");
        saved.setPassword("hashed");

        Mockito.when(userService.register(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString()
        )).thenReturn(saved);

        String body = """
                {
                  "username": "%s",
                  "password": "password123",
                  "displayName": "表示名"
                }
                """.formatted(username100);

        mockMvc.perform(post(POST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void register_returns400_whenUsernameIs101() throws Exception {
        String username101 = repeat('u', 101);

        String body = """
                {
                  "username": "%s",
                  "password": "password123",
                  "displayName": "表示名"
                }
                """.formatted(username101);

        mockMvc.perform(post(POST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors.username").exists());
    }

    @Test
    void register_returns400_whenPasswordIs7() throws Exception {
        var body = """
                {
                  "username": "user01",
                  "password": "1234567",
                  "displayName": "表示名"
                }
                """;

        mockMvc.perform(post(POST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors.password").exists());
    }

    @Test
    void register_returns2xx_whenPasswordIs100() throws Exception {
        String pass100 = repeat('p', 100);

        User saved = new User();
        saved.setId(1L);
        saved.setUsername("user01");
        saved.setDisplayName("表示名");
        saved.setPassword("hashed");

        Mockito.when(userService.register(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString()
        )).thenReturn(saved);

        String body = """
                {
                  "username": "user01",
                  "password": "%s",
                  "displayName": "表示名"
                }
                """.formatted(pass100);

        mockMvc.perform(post(POST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void register_returns400_whenPasswordIs101() throws Exception {
        String pass101 = repeat('p', 101);

        String body = """
                {
                  "username": "user01",
                  "password": "%s",
                  "displayName": "表示名"
                }
                """.formatted(pass101);

        mockMvc.perform(post(POST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors.password").exists());
    }

    @Test
    void register_returns2xx_whenDisplayNameIs100() throws Exception {
        String dn100 = repeat('d', 100);

        User saved = new User();
        saved.setId(1L);
        saved.setUsername("user01");
        saved.setDisplayName(dn100);
        saved.setPassword("hashed");

        Mockito.when(userService.register(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString()
        )).thenReturn(saved);

        String body = """
                {
                  "username": "user01",
                  "password": "password123",
                  "displayName": "%s"
                }
                """.formatted(dn100);

        mockMvc.perform(post(POST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void register_returns400_whenDisplayNameIs101() throws Exception {
        String dn101 = repeat('d', 101);

        String body = """
                {
                  "username": "user01",
                  "password": "password123",
                  "displayName": "%s"
                }
                """.formatted(dn101);

        mockMvc.perform(post(POST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors.displayName").exists());
    }
}