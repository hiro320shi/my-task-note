package main.java.com.example.mytasknote.domain.repository;

import main.java.com.example.mytasknote.domain.entity.Task;
import main.java.com.example.mytasknote.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserOrderByCreatedAtDesc(User user);
}
