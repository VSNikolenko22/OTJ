package org.example.taskmanager.service;

import org.example.taskmanager.model.AppUser;
import org.example.taskmanager.model.Comment;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    private CommentService commentService;
    private AppUser currentUser;

    @BeforeEach
    void setUp() {
        currentUser = new AppUser(1L, "student", "encoded-password", UserRole.USER);
        commentService = new CommentService(commentRepository, taskRepository, userRepository);

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
    void getCommentsByTaskIdReturnsCommentsWhenTaskBelongsToCurrentUser() {
        Task task = createTask(1L);
        Comment comment = new Comment(1L, 1L, "Important comment", null);

        when(taskRepository.findByIdAndUser(1L, currentUser)).thenReturn(Optional.of(task));
        when(commentRepository.findByTaskId(1L)).thenReturn(List.of(comment));

        List<Comment> result = commentService.getCommentsByTaskId(1L);

        assertEquals(1, result.size());
        assertEquals("Important comment", result.get(0).getText());
    }

    @Test
    void getCommentsByTaskIdReturnsEmptyListWhenTaskDoesNotBelongToCurrentUser() {
        when(taskRepository.findByIdAndUser(99L, currentUser)).thenReturn(Optional.empty());

        List<Comment> result = commentService.getCommentsByTaskId(99L);

        assertTrue(result.isEmpty());
        verify(commentRepository, never()).findByTaskId(99L);
    }

    @Test
    void addCommentSavesCommentWhenTaskBelongsToCurrentUser() {
        Task task = createTask(1L);
        Comment comment = new Comment();
        comment.setText("New comment");

        when(taskRepository.findByIdAndUser(1L, currentUser)).thenReturn(Optional.of(task));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Comment savedComment = commentService.addComment(1L, comment);

        assertNotNull(savedComment);
        assertEquals(1L, savedComment.getTaskId());
        assertEquals("New comment", savedComment.getText());
        assertNotNull(savedComment.getCreatedAt());
        verify(commentRepository).save(comment);
    }

    @Test
    void addCommentReturnsNullWhenTaskDoesNotBelongToCurrentUser() {
        Comment comment = new Comment();
        comment.setText("Forbidden comment");

        when(taskRepository.findByIdAndUser(88L, currentUser)).thenReturn(Optional.empty());

        Comment savedComment = commentService.addComment(88L, comment);

        assertNull(savedComment);
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void deleteCommentDeletesCommentWhenRelatedTaskBelongsToCurrentUser() {
        Task task = createTask(1L);
        Comment comment = new Comment(7L, 1L, "Remove this", null);

        when(commentRepository.findById(7L)).thenReturn(Optional.of(comment));
        when(taskRepository.findByIdAndUser(1L, currentUser)).thenReturn(Optional.of(task));

        boolean deleted = commentService.deleteComment(7L);

        assertTrue(deleted);
        verify(commentRepository).delete(comment);
    }

    @Test
    void deleteCommentReturnsFalseWhenRelatedTaskDoesNotBelongToCurrentUser() {
        Comment comment = new Comment(7L, 55L, "Foreign comment", null);

        when(commentRepository.findById(7L)).thenReturn(Optional.of(comment));
        when(taskRepository.findByIdAndUser(55L, currentUser)).thenReturn(Optional.empty());

        boolean deleted = commentService.deleteComment(7L);

        assertFalse(deleted);
        verify(commentRepository, never()).delete(any(Comment.class));
    }

    private Task createTask(Long id) {
        Task task = new Task(id, "Task", "Description", TaskPriority.MEDIUM,
                TaskStatus.TODO, LocalDate.now().plusDays(1));
        task.setUser(currentUser);
        return task;
    }
}
