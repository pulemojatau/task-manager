package com.pule.task_manager_api.service;

import com.pule.task_manager_api.dto.CreateTaskRequest;
import com.pule.task_manager_api.dto.TaskResponse;
import com.pule.task_manager_api.dto.UpdateTaskRequest;
import com.pule.task_manager_api.entity.Task;
import com.pule.task_manager_api.entity.User;
import com.pule.task_manager_api.repository.TaskRepository;
import com.pule.task_manager_api.repository.UserRepository;
import com.pule.task_manager_api.security.SecurityUtil;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

import org.springframework.data.domain.Sort;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    // Create task — assigns the logged-in user as the task creator
 

public TaskResponse createTask(CreateTaskRequest request) {

    // Get the email of the currently authenticated user from our SecurityUtil helper
    String email = SecurityUtil.getCurrentUserEmail();

    // Look up the full User entity from the database using the email
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

// Get tasks belonging to the currently logged-in user (with pagination, filtering, sorting)
public List<TaskResponse> getMyTasks(
        int page,
        int size,
        String status,
        String sortBy,
        String direction
) {

    // 1. Get logged-in user's email using SecurityUtil helper
    String email = SecurityUtil.getCurrentUserEmail();

    // 2. Look up the full User entity from the database
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    // 2. Create Sort object
    Sort sort = direction.equalsIgnoreCase("ASC")
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();

    // 3. Create pageable WITH sorting
    Pageable pageable = PageRequest.of(page, size, sort);

    Page<Task> tasksPage;

    // 4. Filtering logic
    if (status != null && !status.isEmpty()) {
        tasksPage = taskRepository.findByCreatedByAndStatus(user, status, pageable);
    } else {
        tasksPage = taskRepository.findByCreatedBy(user, pageable);
    }

    // 5. Convert to DTOs
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

// Update a task — only the user who created it can update it
public TaskResponse updateTask(Long taskId, UpdateTaskRequest request) {

    // Get the currently authenticated user's email via SecurityUtil helper
    String email = SecurityUtil.getCurrentUserEmail();

    // Find the logged-in user in the database
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    // Find the task to update
    Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task not found"));

    // 🔐 Authorization check — only the creator can update their own task
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

// Delete a task — only the user who created it can delete it
public void deleteTask(Long taskId) {

    // Get the currently authenticated user's email via SecurityUtil helper
    String email = SecurityUtil.getCurrentUserEmail();

    // Find the logged-in user in the database
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    // Find the task to delete
    Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task not found"));

    // 🔐 Authorization check — only the creator can delete their own task
    if (!task.getCreatedBy().getId().equals(user.getId())) {
        throw new RuntimeException("You are not allowed to delete this task");
    }

    taskRepository.delete(task);
}

}