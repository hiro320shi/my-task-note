package com.example.mytasknote.task.repository;

import com.example.mytasknote.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // ログインユーザごとのタスク取得
    List<Task> findByUserUsernameOrderByCreatedAtDesc(String username);

    // 更新/削除のときに「自分のタスクか」をチェックする用
    Optional<Task> findByIdAndUserUsername(Long id, String username);
}