package org.example.taskmanager.controller;

import org.example.taskmanager.model.Task;
import org.example.taskmanager.model.TaskPriority;
import org.example.taskmanager.model.TaskStatus;
import org.example.taskmanager.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService){
        this.taskService = taskService;
    }

    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);

        if (task == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(task);
    }

    @PostMapping
    public Task createTask(@RequestBody Task task) {
        return taskService.createTask(task);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task task) {
        Task updatedTask = taskService.updateTask(id, task);

        if (updatedTask == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable Long id) {
        boolean deleted = taskService.deleteTask(id);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }


        return ResponseEntity.ok("Task deleted successfully");
    }
    @PatchMapping("/{id}/status")
    public ResponseEntity<Task> updateTaskStatus(@PathVariable Long id, @RequestParam TaskStatus status) {
        Task task = taskService.updateTaskStatus(id, status);

        if (task == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(task);
    }

    @GetMapping("/filter/status")
    public List<Task> getTasksByStatus(@RequestParam TaskStatus status) {
        return taskService.getTasksByStatus(status);
    }

    @GetMapping("/filter/priority")
    public List<Task> getTasksByPriority(@RequestParam TaskPriority priority) {
        return taskService.getTasksByPriority(priority);
    }

    @GetMapping("/search")
    public List<Task> searchTasksByTitle(@RequestParam String keyword) {
        return taskService.searchTasksByTitle(keyword);
    }


    @GetMapping("/sort/deadline")
    public List<Task> getTasksSortedByDeadline() {
        return taskService.getTasksSortedByDeadline();
    }

    @GetMapping("/sort/priority")
    public List<Task> getTasksSortedByPriority() {
        return taskService.getTasksSortedByPriority();
    }
}