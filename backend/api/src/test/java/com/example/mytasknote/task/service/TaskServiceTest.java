package com.example.mytasknote.task.service;

import com.example.mytasknote.common.exception.NotFoundException;
import com.example.mytasknote.task.dto.TaskCreateRequest;
import com.example.mytasknote.task.dto.TaskResponse;
import com.example.mytasknote.task.dto.TaskUpdateRequest;
import com.example.mytasknote.task.entity.Task;
import com.example.mytasknote.task.repository.TaskRepository;
import com.example.mytasknote.user.entity.User;
import com.example.mytasknote.user.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    TaskRepository taskRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    TaskService taskService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(10L);
        user.setUsername("testuser01");
        user.setDisplayName("表示名");
        user.setPassword("hashed");
    }

    // ----------------------------
    // createTask
    // ----------------------------

    @Test
    void createTask_setsFields_andReturnsResponse_dueDatePresent() {
        // arrange
        TaskCreateRequest req = new TaskCreateRequest();
        req.setTitle("title");
        req.setDescription("desc");
        req.setDueDate(LocalDate.of(2025, 12, 31));

        when(userRepository.findByUsername("testuser01")).thenReturn(Optional.of(user));

        // saveされるTaskを捕まえて、返却用にidを付ける
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> {
            Task t = inv.getArgument(0);
            t.setId(1L);
            return t;
        });

        // act
        TaskResponse res = taskService.createTask("testuser01", req);

        // assert
        assertEquals(1L, res.getId());
        assertEquals("title", res.getTitle());
        assertEquals("desc", res.getDescription());
        assertEquals("2025-12-31", res.getDueDate());
        assertFalse(res.isCompleted());

        // 保存されたTaskの中身も確認
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());

        Task saved = captor.getValue();
        assertSame(user, saved.getUser());
        assertEquals("title", saved.getTitle());
        assertEquals("desc", saved.getDescription());
        assertEquals(LocalDate.of(2025, 12, 31), saved.getDueDate());
        assertFalse(saved.isCompleted());
    }

    @Test
    void createTask_setsDueDateNull_whenDueDateNull() {
        TaskCreateRequest req = new TaskCreateRequest();
        req.setTitle("title");
        req.setDescription("desc");
        req.setDueDate(null);

        when(userRepository.findByUsername("testuser01")).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> {
            Task t = inv.getArgument(0);
            t.setId(1L);
            return t;
        });

        TaskResponse res = taskService.createTask("testuser01", req);

        assertNull(res.getDueDate());

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());
        assertNull(captor.getValue().getDueDate());
    }

    @Test
    void createTask_throwsNotFound_whenUserNotFound() {
        TaskCreateRequest req = new TaskCreateRequest();
        req.setTitle("title");
        req.setDescription("desc");
        req.setDueDate(LocalDate.of(2025, 12, 31));

        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> taskService.createTask("missing", req));

        assertTrue(ex.getMessage().contains("User not found"));
        verify(taskRepository, never()).save(any());
    }

    // ----------------------------
    // listTasks
    // ----------------------------

    @Test
    void listTasks_mapsEntitiesToResponses() {
        Task t1 = new Task();
        t1.setId(1L);
        t1.setTitle("t1");
        t1.setDescription("d1");
        t1.setDueDate(null);
        t1.setCompleted(false);

        Task t2 = new Task();
        t2.setId(2L);
        t2.setTitle("t2");
        t2.setDescription("d2");
        t2.setDueDate(LocalDate.of(2025, 12, 31));
        t2.setCompleted(true);

        when(taskRepository.findByUserUsernameOrderByCreatedAtDesc("testuser01"))
                .thenReturn(List.of(t1, t2));

        List<TaskResponse> res = taskService.listTasks("testuser01");

        assertEquals(2, res.size());
        assertEquals(1L, res.get(0).getId());
        assertNull(res.get(0).getDueDate());
        assertEquals("2025-12-31", res.get(1).getDueDate());
        assertTrue(res.get(1).isCompleted());
    }

    // ----------------------------
    // updateTask
    // ----------------------------

    @Test
    void updateTask_updatesProvidedFields_andParsesDueDate() {
        Task existing = new Task();
        existing.setId(1L);
        existing.setUser(user);
        existing.setTitle("old");
        existing.setDescription("oldDesc");
        existing.setDueDate(null);
        existing.setCompleted(false);

        when(taskRepository.findByIdAndUserUsername(1L, "testuser01"))
                .thenReturn(Optional.of(existing));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        TaskUpdateRequest req = new TaskUpdateRequest();
        req.setTitle("new");
        req.setDescription("newDesc");
        req.setDueDate("2025-12-31");
        req.setCompleted(true);

        TaskResponse res = taskService.updateTask("testuser01", 1L, req);

        assertEquals("new", res.getTitle());
        assertEquals("newDesc", res.getDescription());
        assertEquals("2025-12-31", res.getDueDate());
        assertTrue(res.isCompleted());

        // 実体も更新されていること
        assertEquals(LocalDate.of(2025, 12, 31), existing.getDueDate());
    }

    @Test
    void updateTask_doesNotChangeDueDate_whenDueDateIsNull() {
        Task existing = new Task();
        existing.setId(1L);
        existing.setUser(user);
        existing.setTitle("old");
        existing.setDescription("oldDesc");
        existing.setDueDate(LocalDate.of(2025, 1, 1));
        existing.setCompleted(false);

        when(taskRepository.findByIdAndUserUsername(1L, "testuser01"))
                .thenReturn(Optional.of(existing));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        TaskUpdateRequest req = new TaskUpdateRequest();
        req.setDueDate(null); // 未指定（変更しない）

        taskService.updateTask("testuser01", 1L, req);

        assertEquals(LocalDate.of(2025, 1, 1), existing.getDueDate());
    }

    @Test
    void updateTask_clearsDueDate_whenDueDateBlank() {
        Task existing = new Task();
        existing.setId(1L);
        existing.setUser(user);
        existing.setTitle("old");
        existing.setDescription("oldDesc");
        existing.setDueDate(LocalDate.of(2025, 1, 1));
        existing.setCompleted(false);

        when(taskRepository.findByIdAndUserUsername(1L, "testuser01"))
                .thenReturn(Optional.of(existing));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        TaskUpdateRequest req = new TaskUpdateRequest();
        req.setDueDate("   "); // blank -> clear

        taskService.updateTask("testuser01", 1L, req);

        assertNull(existing.getDueDate());
    }

    @Test
    void updateTask_doesNotChangeTitle_whenTitleNull() {
        Task existing = new Task();
        existing.setId(1L);
        existing.setUser(user);
        existing.setTitle("old");
        existing.setDescription("oldDesc");
        existing.setDueDate(null);
        existing.setCompleted(false);

        when(taskRepository.findByIdAndUserUsername(1L, "testuser01"))
                .thenReturn(Optional.of(existing));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        TaskUpdateRequest req = new TaskUpdateRequest();
        req.setTitle(null); // 変更しない
        req.setDescription("newDesc");

        taskService.updateTask("testuser01", 1L, req);

        assertEquals("old", existing.getTitle());
        assertEquals("newDesc", existing.getDescription());
    }

    @Test
    void updateTask_throwsNotFound_whenTaskNotFound() {
        when(taskRepository.findByIdAndUserUsername(999L, "testuser01"))
                .thenReturn(Optional.empty());

        TaskUpdateRequest req = new TaskUpdateRequest();
        req.setTitle("x");

        assertThrows(NotFoundException.class,
                () -> taskService.updateTask("testuser01", 999L, req));

        verify(taskRepository, never()).save(any());
    }

    // ----------------------------
    // deleteTask
    // ----------------------------

    @Test
    void deleteTask_deletesWhenFound() {
        Task existing = new Task();
        existing.setId(1L);

        when(taskRepository.findByIdAndUserUsername(1L, "testuser01"))
                .thenReturn(Optional.of(existing));

        taskService.deleteTask("testuser01", 1L);

        verify(taskRepository).delete(existing);
    }

    @Test
    void deleteTask_throwsNotFound_whenTaskNotFound() {
        when(taskRepository.findByIdAndUserUsername(999L, "testuser01"))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> taskService.deleteTask("testuser01", 999L));

        verify(taskRepository, never()).delete(any());
    }
}