package com.example.mytasknote.domain.repository;

import com.example.mytasknote.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    // ★ 変更
    boolean existsByUsername(String username);
}
