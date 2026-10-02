package org.example.taskmanager.service;

import org.example.taskmanager.model.AppUser;
import org.example.taskmanager.model.Task;
import org.example.taskmanager.model.TaskPriority;
import org.example.taskmanager.model.TaskStatus;
import org.example.taskmanager.repository.CommentRepository;
import org.example.taskmanager.repository.TaskRepository;
import org.example.taskmanager.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository,
                       CommentRepository commentRepository,
                       UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    private AppUser getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }

    public List<Task> getAllTasks() {
        return taskRepository.findByUser(getCurrentUser());
    }

    public Task getTaskById(Long id) {
        return taskRepository.findByIdAndUser(id, getCurrentUser()).orElse(null);
    }

    public Task createTask(Task task) {
        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.TODO);
        }

        if (task.getPriority() == null) {
            task.setPriority(TaskPriority.MEDIUM);
        }

        task.setUser(getCurrentUser());

        return taskRepository.save(task);
    }

    public Task updateTask(Long id, Task updatedTask) {
        Task existingTask = getTaskById(id);

        if (existingTask == null) {
            return null;
        }

        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setPriority(updatedTask.getPriority());
        existingTask.setStatus(updatedTask.getStatus());
        existingTask.setDeadline(updatedTask.getDeadline());

        return taskRepository.save(existingTask);
    }

    @Transactional
    public boolean deleteTask(Long id) {
        Task task = getTaskById(id);

        if (task == null) {
            return false;
        }

        commentRepository.deleteByTaskId(id);
        taskRepository.delete(task);

        return true;
    }

    public Task updateTaskStatus(Long id, TaskStatus status) {
        Task task = getTaskById(id);

        if (task == null) {
            return null;
        }

        task.setStatus(status);
        return taskRepository.save(task);
    }

    public List<Task> getTasksByStatus(TaskStatus status) {
        List<Task> result = new ArrayList<>();

        for (Task task : getAllTasks()) {
            if (task.getStatus() == status) {
                result.add(task);
            }
        }

        return result;
    }

    public List<Task> getTasksByPriority(TaskPriority priority) {
        List<Task> result = new ArrayList<>();

        for (Task task : getAllTasks()) {
            if (task.getPriority() == priority) {
                result.add(task);
            }
        }

        return result;
    }

    public List<Task> searchTasksByTitle(String keyword) {
        List<Task> result = new ArrayList<>();

        for (Task task : getAllTasks()) {
            if (task.getTitle() != null &&
                    task.getTitle().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(task);
            }
        }

        return result;
    }

    public List<Task> getTasksSortedByDeadline() {
        List<Task> result = new ArrayList<>(getAllTasks());

        result.sort((task1, task2) -> {
            if (task1.getDeadline() == null && task2.getDeadline() == null) {
                return 0;
            }

            if (task1.getDeadline() == null) {
                return 1;
            }

            if (task2.getDeadline() == null) {
                return -1;
            }

            return task1.getDeadline().compareTo(task2.getDeadline());
        });

        return result;
    }

    public List<Task> getTasksSortedByPriority() {
        List<Task> result = new ArrayList<>(getAllTasks());

        result.sort(Comparator.comparingInt(task -> -getPriorityValue(task.getPriority())));

        return result;
    }

    private int getPriorityValue(TaskPriority priority) {
        if (priority == TaskPriority.HIGH) {
            return 3;
        }

        if (priority == TaskPriority.MEDIUM) {
            return 2;
        }

        return 1;
    }
}