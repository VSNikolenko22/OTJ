package org.example.taskmanager.service;

import org.example.taskmanager.model.AppUser;
import org.example.taskmanager.model.Task;
import org.example.taskmanager.model.TaskPriority;
import org.example.taskmanager.model.TaskStatus;
import org.example.taskmanager.model.UserRole;
import org.example.taskmanager.repository.CommentRepository;
import org.example.taskmanager.repository.TaskRepository;
import org.example.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    private TaskService taskService;
    private AppUser currentUser;

    @BeforeEach
    void setUp() {
        currentUser = new AppUser(1L, "student", "encoded-password", UserRole.USER);
        taskService = new TaskService(taskRepository, commentRepository, userRepository);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("student", "password")
        );

        when(userRepository.findByUsername("student")).thenReturn(Optional.of(currentUser));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createTaskSetsCurrentUserAndDefaultValues() {
        Task task = new Task();
        task.setTitle("Prepare documentation");

        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task savedTask = taskService.createTask(task);

        assertSame(currentUser, savedTask.getUser());
        assertEquals(TaskStatus.TODO, savedTask.getStatus());
        assertEquals(TaskPriority.MEDIUM, savedTask.getPriority());
        verify(taskRepository).save(task);
    }

    @Test
    void getTaskByIdReturnsOnlyTaskOwnedByCurrentUser() {
        Task task = new Task(10L, "Learn Spring", "Unit tests", TaskPriority.HIGH,
                TaskStatus.IN_PROGRESS, LocalDate.now().plusDays(2));

        when(taskRepository.findByIdAndUser(10L, currentUser)).thenReturn(Optional.of(task));

        Task result = taskService.getTaskById(10L);

        assertSame(task, result);
        verify(taskRepository).findByIdAndUser(10L, currentUser);
    }

    @Test
    void searchTasksByTitleSearchesOnlyCurrentUserTasks() {
        Task springTask = new Task(1L, "Learn Spring Boot", "Backend",
                TaskPriority.HIGH, TaskStatus.TODO, LocalDate.now());
        Task htmlTask = new Task(2L, "Create HTML page", "Frontend",
                TaskPriority.MEDIUM, TaskStatus.TODO, LocalDate.now());

        when(taskRepository.findByUser(currentUser)).thenReturn(List.of(springTask, htmlTask));

        List<Task> result = taskService.searchTasksByTitle("spring");

        assertEquals(1, result.size());
        assertEquals("Learn Spring Boot", result.get(0).getTitle());
    }

    @Test
    void getTasksSortedByPriorityOrdersHighMediumLow() {
        Task low = new Task(1L, "Low task", "", TaskPriority.LOW, TaskStatus.TODO, null);
        Task high = new Task(2L, "High task", "", TaskPriority.HIGH, TaskStatus.TODO, null);
        Task medium = new Task(3L, "Medium task", "", TaskPriority.MEDIUM, TaskStatus.TODO, null);

        when(taskRepository.findByUser(currentUser)).thenReturn(List.of(low, high, medium));

        List<Task> result = taskService.getTasksSortedByPriority();

        assertEquals(TaskPriority.HIGH, result.get(0).getPriority());
        assertEquals(TaskPriority.MEDIUM, result.get(1).getPriority());
        assertEquals(TaskPriority.LOW, result.get(2).getPriority());
    }

    @Test
    void deleteTaskDeletesTaskAndItsCommentsWhenTaskBelongsToCurrentUser() {
        Task task = new Task(5L, "Delete me", "", TaskPriority.LOW, TaskStatus.TODO, null);

        when(taskRepository.findByIdAndUser(5L, currentUser)).thenReturn(Optional.of(task));

        boolean deleted = taskService.deleteTask(5L);

        assertTrue(deleted);
        verify(commentRepository).deleteByTaskId(5L);
        verify(taskRepository).delete(task);
    }

    @Test
    void deleteTaskReturnsFalseWhenTaskDoesNotBelongToCurrentUser() {
        when(taskRepository.findByIdAndUser(99L, currentUser)).thenReturn(Optional.empty());

        boolean deleted = taskService.deleteTask(99L);

        assertFalse(deleted);
        verify(commentRepository, never()).deleteByTaskId(99L);
        verify(taskRepository, never()).delete(any(Task.class));
    }
}
