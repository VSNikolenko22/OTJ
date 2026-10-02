package org.example.taskmanager.repository;

import org.example.taskmanager.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByTaskId(Long taskId);

    void deleteByTaskId(Long taskId);

}