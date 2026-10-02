package org.example.taskmanager.controller;

import org.example.taskmanager.model.Comment;
import org.example.taskmanager.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/tasks/{taskId}/comments")
    public List<Comment> getCommentsByTaskId(@PathVariable Long taskId) {
        return commentService.getCommentsByTaskId(taskId);
    }

    @PostMapping("/tasks/{taskId}/comments")
    public ResponseEntity<Comment> addComment(@PathVariable Long taskId, @RequestBody Comment comment) {
        Comment savedComment = commentService.addComment(taskId, comment);

        if (savedComment == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(savedComment);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
        boolean deleted = commentService.deleteComment(commentId);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok("Comment deleted successfully");
    }
}