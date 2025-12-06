package com.example.mytasknote.web;

import com.example.mytasknote.domain.entity.User;
import com.example.mytasknote.domain.service.UserService;
import com.example.mytasknote.web.dto.UserRegisterRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public ResponseEntity<Void> registerUser(
            @Valid @RequestBody UserRegisterRequest request) {

        User user = userService.register(
                request.getUsername(),          // ★ username
                request.getPassword(),
                request.getDisplayName()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Location", "/api/users/" + user.getId())
                .build();
    }
}
