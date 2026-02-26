package com.example.mytasknote.auth.controller;

import com.example.mytasknote.auth.jwt.JwtAuthenticationFilter;
import com.example.mytasknote.auth.service.AuthService;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // セキュリティフィルタはテストでは無効化
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AuthService authService;

    // SecurityConfig が要求するフィルタをモック
    @MockBean
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void login_returns200_whenSuccess() throws Exception {
        Mockito.when(authService.login(
                ArgumentMatchers.eq("testuser01"),
                ArgumentMatchers.eq("password123")
        )).thenReturn("token-abc");

        var body = """
                {
                "username": "testuser01",
                "password": "password123"
                }
                """;

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("token-abc"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void login_returns401_whenBadCredentials() throws Exception {
        Mockito.when(authService.login(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString()
        )).thenThrow(new BadCredentialsException("Invalid username or password"));

        var body = """
                {
                  "username": "wrong",
                  "password": "wrong"
                }
                """;

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("BAD_CREDENTIALS"))
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    void login_returns400_whenValidationError() throws Exception {
        var body = """
                {
                  "username": "",
                  "password": ""
                }
                """;

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.username").exists());
    }
}