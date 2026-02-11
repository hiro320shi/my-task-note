package com.example.mytasknote.auth.controller;

import com.example.mytasknote.auth.service.AuthService;
import com.example.mytasknote.auth.dto.LoginRequest;
import com.example.mytasknote.auth.dto.LoginResponse;
import com.example.mytasknote.auth.jwt.JwtAuthenticationFilter;
import com.example.mytasknote.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        String token = authService.login(request.getUsername(), request.getPassword());

        return ResponseEntity.ok(new LoginResponse(token));
    }
}
