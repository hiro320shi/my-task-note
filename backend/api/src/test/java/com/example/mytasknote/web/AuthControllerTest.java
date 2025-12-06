package com.example.mytasknote.web;

import com.example.mytasknote.config.JwtAuthenticationFilter;
import com.example.mytasknote.domain.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.example.mytasknote.web.AuthController;

import org.junit.jupiter.api.Test;
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
@AutoConfigureMockMvc(addFilters = false) // ★ セキュリティフィルタはテストでは無効化
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    AuthService authService;  // ★ AuthController が使うサービスをモック

    @MockBean
    JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Test
    void login_success_returns200_andToken() throws Exception {
        // arrange
        Mockito.when(authService.login("testuser01", "password123"))
                .thenReturn("dummy-jwt-token");

        var body = """
                {
                  "username": "testuser01",
                  "password": "password123"
                }
                """;

        // act & assert
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("dummy-jwt-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void login_badCredentials_returns401() throws Exception {
        // arrange
        Mockito.when(authService.login("wronguser", "wrongpass"))
                .thenThrow(new BadCredentialsException("Invalid username or password"));

        var body = """
                {
                  "username": "wronguser",
                  "password": "wrongpass"
                }
                """;

        // act & assert
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    void login_validationError_returns400() throws Exception {
        // username, password ともに空 → @Valid に引っかかる
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
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }
}
