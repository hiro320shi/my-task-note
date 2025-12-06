package com.example.mytasknote.web;

import com.example.mytasknote.domain.service.AuthService;
import com.example.mytasknote.web.dto.LoginRequest;
import com.example.mytasknote.web.dto.LoginResponse;
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
