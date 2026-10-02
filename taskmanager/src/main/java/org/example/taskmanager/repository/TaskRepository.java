package org.example.taskmanager.repository;

import org.example.taskmanager.model.AppUser;
import org.example.taskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByUser(AppUser user);

    Optional<Task> findByIdAndUser(Long id, AppUser user);
}