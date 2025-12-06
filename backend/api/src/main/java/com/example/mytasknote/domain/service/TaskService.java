package com.example.mytasknote.domain.service;

import com.example.mytasknote.domain.entity.Task;
import com.example.mytasknote.domain.entity.User;
import com.example.mytasknote.domain.repository.TaskRepository;
import com.example.mytasknote.domain.repository.UserRepository;
import com.example.mytasknote.web.dto.TaskCreateRequest;
import com.example.mytasknote.web.dto.TaskResponse;
import com.example.mytasknote.web.dto.TaskUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository,
                       UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    // ログインユーザのタスク登録
    @Transactional
    public TaskResponse createTask(String username, TaskCreateRequest request) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        Task task = new Task();
        task.setUser(user);
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());

        if (request.getDueDate() != null && !request.getDueDate().isBlank()) {
            task.setDueDate(LocalDate.parse(request.getDueDate())); // "YYYY-MM-DD"
        }

        task.setCompleted(false);

        Task saved = taskRepository.save(task);

        return toResponse(saved);
    }

    // ログインユーザのタスク一覧
    @Transactional(readOnly = true)
    public List<TaskResponse> listTasks(String username) {
        return taskRepository.findByUserUsernameOrderByCreatedAtDesc(username)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // 更新
    @Transactional
    public TaskResponse updateTask(String username, Long taskId, TaskUpdateRequest request) {
        Task task = taskRepository.findByIdAndUserUsername(taskId, username)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getDueDate() != null) {
            if (request.getDueDate().isBlank()) {
                task.setDueDate(null);
            } else {
                task.setDueDate(LocalDate.parse(request.getDueDate()));
            }
        }
        if (request.getCompleted() != null) {
            task.setCompleted(request.getCompleted());
        }

        Task saved = taskRepository.save(task);
        return toResponse(saved);
    }

    // 削除
    @Transactional
    public void deleteTask(String username, Long taskId) {
        Task task = taskRepository.findByIdAndUserUsername(taskId, username)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        taskRepository.delete(task);
    }

    private TaskResponse toResponse(Task task) {
        String due = task.getDueDate() != null ? task.getDueDate().toString() : null;
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                due,
                task.isCompleted()
        );
    }
}
