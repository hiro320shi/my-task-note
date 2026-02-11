package com.example.mytasknote.user.repository;

import com.example.mytasknote.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // ★ 変更
    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);
}
