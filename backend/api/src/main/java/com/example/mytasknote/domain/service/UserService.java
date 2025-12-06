package com.example.mytasknote.domain.service;

import com.example.mytasknote.domain.entity.User;
import com.example.mytasknote.domain.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(String username, String rawPassword, String displayName) {

        // ★ 変更: existsByEmail → existsByUsername
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateUsernameException(username);
        }

        User user = new User();
        user.setUsername(username);                             // ★ 変更: setEmail → setUsername
        user.setPassword(passwordEncoder.encode(rawPassword));  // password カラム
        user.setDisplayName(displayName);

        return userRepository.save(user);
    }
}
