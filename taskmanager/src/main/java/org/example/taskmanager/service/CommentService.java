package org.example.taskmanager.service;

import org.example.taskmanager.model.AppUser;
import org.example.taskmanager.model.Comment;
import org.example.taskmanager.model.Task;
import org.example.taskmanager.repository.CommentRepository;
import org.example.taskmanager.repository.TaskRepository;
import org.example.taskmanager.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository,
                          TaskRepository taskRepository,
                          UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    private AppUser getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }

    public List<Comment> getCommentsByTaskId(Long taskId) {
        Task task = taskRepository.findByIdAndUser(taskId, getCurrentUser()).orElse(null);

        if (task == null) {
            return List.of();
        }

        return commentRepository.findByTaskId(taskId);
    }

    public Comment addComment(Long taskId, Comment comment) {
        Task task = taskRepository.findByIdAndUser(taskId, getCurrentUser()).orElse(null);

        if (task == null) {
            return null;
        }

        comment.setTaskId(taskId);
        comment.setCreatedAt(LocalDateTime.now());

        return commentRepository.save(comment);
    }

    public boolean deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElse(null);

        if (comment == null) {
            return false;
        }

        Task task = taskRepository.findByIdAndUser(comment.getTaskId(), getCurrentUser()).orElse(null);

        if (task == null) {
            return false;
        }

        commentRepository.delete(comment);
        return true;
    }



}