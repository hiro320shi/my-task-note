package com.example.mytasknote.auth.service;

import com.example.mytasknote.auth.jwt.JwtService;
import com.example.mytasknote.user.entity.User;
import com.example.mytasknote.user.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtService jwtService;

    @InjectMocks
    AuthService authService;

    @Test
    void login_throwsBadCredentials_whenUserNotFound() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class,
                () -> authService.login("missing", "pw"));

        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    void login_throwsBadCredentials_whenPasswordMismatch() {
        User user = new User();
        user.setUsername("testuser01");
        user.setPassword("hashed");

        when(userRepository.findByUsername("testuser01")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        assertThrows(BadCredentialsException.class,
                () -> authService.login("testuser01", "wrong"));

        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    void login_returnsToken_whenSuccess() {
        User user = new User();
        user.setUsername("testuser01");
        user.setPassword("hashed");

        when(userRepository.findByUsername("testuser01")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashed")).thenReturn(true);
        when(jwtService.generateToken("testuser01")).thenReturn("token-abc");

        String token = authService.login("testuser01", "password123");

        assertEquals("token-abc", token);
        verify(jwtService).generateToken("testuser01");
    }
}