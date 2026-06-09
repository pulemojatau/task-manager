package com.pule.task_manager_api.controller;

import com.pule.task_manager_api.dto.CreateTaskRequest;
import com.pule.task_manager_api.dto.TaskResponse;
import com.pule.task_manager_api.dto.UpdateTaskRequest;
import com.pule.task_manager_api.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // Create task
    @PostMapping
    public TaskResponse createTask(@Valid @RequestBody CreateTaskRequest request) {
        return taskService.createTask(request);
    }

    //Get tasks
@GetMapping
public List<TaskResponse> getMyTasks(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size,
        @RequestParam(required = false) String status,
        @RequestParam(defaultValue = "createdAt") String sortBy,
        @RequestParam(defaultValue = "DESC") String direction
) {
    return taskService.getMyTasks(page, size, status, sortBy, direction);
}

    @PutMapping("/{taskId}")
public TaskResponse updateTask(
        @PathVariable Long taskId,
        @Valid @RequestBody UpdateTaskRequest request
) {
    return taskService.updateTask(taskId, request);
}

@DeleteMapping("/{taskId}")
public String deleteTask(@PathVariable Long taskId) {

    taskService.deleteTask(taskId);

    return "Task deleted successfully";
}

}