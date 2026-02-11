package com.example.mytasknote.task.service;

import com.example.mytasknote.task.entity.Task;
import com.example.mytasknote.user.entity.User;
import com.example.mytasknote.task.repository.TaskRepository;
import com.example.mytasknote.user.repository.UserRepository;

import com.example.mytasknote.common.exception.NotFoundException;

import com.example.mytasknote.task.dto.TaskCreateRequest;
import com.example.mytasknote.task.dto.TaskResponse;
import com.example.mytasknote.task.dto.TaskUpdateRequest;
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
                    .orElseThrow(() -> new NotFoundException("User not found: " + username));

        Task task = new Task();
        task.setUser(user);
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());

        task.setDueDate(request.getDueDate());

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
                .orElseThrow(() -> new NotFoundException("Task not found"));

        applyTitle(task, request);
        applyDescription(task, request);
        applyDueDate(task, request);
        applyCompleted(task, request);

        Task saved = taskRepository.save(task);
        return toResponse(saved);
    }

    private void applyTitle(Task task, TaskUpdateRequest request) {
        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
    }

    private void applyDescription(Task task, TaskUpdateRequest request) {
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
    }

    private void applyDueDate(Task task, TaskUpdateRequest request) {
        if (request.getDueDate() == null) {
            return; // 未指定なら変更しない
        }
        if (request.getDueDate().isBlank()) {
            task.setDueDate(null); // 空文字はクリア
            return;
        }
        task.setDueDate(LocalDate.parse(request.getDueDate()));
    }

    private void applyCompleted(Task task, TaskUpdateRequest request) {
        if (request.getCompleted() != null) {
            task.setCompleted(request.getCompleted());
        }
    }

    // 削除
    @Transactional
    public void deleteTask(String username, Long taskId) {
        Task task = taskRepository.findByIdAndUserUsername(taskId, username)
                    .orElseThrow(() -> new NotFoundException("Task not found"));

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
