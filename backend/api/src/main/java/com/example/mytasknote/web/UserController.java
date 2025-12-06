package com.example.mytasknote.web;

import com.example.mytasknote.domain.entity.User;
import com.example.mytasknote.domain.repository.UserRepository;
import com.example.mytasknote.domain.service.UserService;
import com.example.mytasknote.web.dto.UserRegisterRequest;
import jakarta.validation.Valid;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService,
                          UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
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

@GetMapping("/users/me")
public ResponseEntity<Map<String, Object>> getMe(Principal principal) {

    // ★ principal が null の場合は 401 を返すようにする
    if (principal == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    String username = principal.getName();

    User user = userRepository.findByUsername(username)
            .orElseThrow(); // TODO: 本当は 404 を返したりするのが丁寧

    Map<String, Object> body = Map.of(
            "id", user.getId(),
            "username", user.getUsername(),
            "displayName", user.getDisplayName()
    );

    return ResponseEntity.ok(body);
}

}
