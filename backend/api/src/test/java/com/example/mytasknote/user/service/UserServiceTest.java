package com.example.mytasknote.user.service;

import com.example.mytasknote.common.exception.DuplicateUsernameException;
import com.example.mytasknote.user.entity.User;
import com.example.mytasknote.user.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;

    @Test
    void register_throwsDuplicate_whenUsernameExists() {
        when(userRepository.existsByUsername("dupuser")).thenReturn(true);

        assertThrows(DuplicateUsernameException.class,
                () -> userService.register("dupuser", "password123", "表示名"));

        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_encodesPassword_andSavesUser_whenNewUsername() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed-pass");

        // saveされるUserをそのまま返しつつ、idが必要なら付ける
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });

        User saved = userService.register("newuser", "password123", "表示名");

        assertNotNull(saved);
        assertEquals(1L, saved.getId());
        assertEquals("newuser", saved.getUsername());
        assertEquals("hashed-pass", saved.getPassword());
        assertEquals("表示名", saved.getDisplayName());

        // saveに渡した中身も確認（より堅く）
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User toSave = captor.getValue();

        assertEquals("newuser", toSave.getUsername());
        assertEquals("hashed-pass", toSave.getPassword());
        assertEquals("表示名", toSave.getDisplayName());
    }
}