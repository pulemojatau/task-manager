package com.pule.task_manager_api.service;

import com.pule.task_manager_api.dto.CreateTaskRequest;
import com.pule.task_manager_api.dto.TaskResponse;
import com.pule.task_manager_api.dto.UpdateTaskRequest;
import com.pule.task_manager_api.entity.Task;
import com.pule.task_manager_api.entity.User;
import com.pule.task_manager_api.repository.TaskRepository;
import com.pule.task_manager_api.repository.UserRepository;

import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    // Create task
 

public TaskResponse createTask(CreateTaskRequest request) {

    String email = SecurityContextHolder.getContext()
            .getAuthentication()
            .getName();

    User creator = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    Task task = new Task();

    task.setTitle(request.getTitle());
    task.setDescription(request.getDescription());
    task.setStatus(request.getStatus());
    task.setDueDate(request.getDueDate());

    task.setCreatedBy(creator);

    // Optional assignment
    if (request.getAssignedToUserId() != null) {

        User assignedUser = userRepository.findById(request.getAssignedToUserId())
                .orElseThrow(() -> new RuntimeException("Assigned user not found"));

        task.setAssignedTo(assignedUser);
    }

    Task savedTask = taskRepository.save(task);

    return mapToResponse(savedTask);
}

public List<TaskResponse> getMyTasks(int page, int size) {

    String email = SecurityContextHolder.getContext()
            .getAuthentication()
            .getName();

    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    Pageable pageable = PageRequest.of(page, size);

    Page<Task> tasksPage = taskRepository.findByCreatedBy(user, pageable);

    return tasksPage.stream()
            .map(this::mapToResponse)
            .toList();
}

private TaskResponse mapToResponse(Task task) {

    TaskResponse response = new TaskResponse();

    response.setId(task.getId());
    response.setTitle(task.getTitle());
    response.setDescription(task.getDescription());
    response.setStatus(task.getStatus());
    response.setDueDate(task.getDueDate());

    response.setCreatedByEmail(
            task.getCreatedBy() != null
                    ? task.getCreatedBy().getEmail()
                    : null
    );

    response.setAssignedToEmail(
            task.getAssignedTo() != null
                    ? task.getAssignedTo().getEmail()
                    : null
    );

    return response;
}

public TaskResponse updateTask(Long taskId, UpdateTaskRequest request) {

    // Get logged-in user email
    String email = SecurityContextHolder.getContext()
            .getAuthentication()
            .getName();

    // Find logged-in user
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    // Find task
    Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task not found"));

    // 🔐 Authorization check
    if (!task.getCreatedBy().getId().equals(user.getId())) {
        throw new RuntimeException("You are not allowed to update this task");
    }

    // Update fields
    task.setTitle(request.getTitle());
    task.setDescription(request.getDescription());
    task.setStatus(request.getStatus());
    task.setDueDate(request.getDueDate());

    Task updatedTask = taskRepository.save(task);

    return mapToResponse(updatedTask);
}

public void deleteTask(Long taskId) {

    String email = SecurityContextHolder.getContext()
            .getAuthentication()
            .getName();

    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task not found"));

    // Authorization check
    if (!task.getCreatedBy().getId().equals(user.getId())) {
        throw new RuntimeException("You are not allowed to delete this task");
    }

    taskRepository.delete(task);
}

}