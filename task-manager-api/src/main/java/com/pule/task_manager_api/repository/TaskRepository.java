package com.pule.task_manager_api.repository;

import com.pule.task_manager_api.entity.Task;
import com.pule.task_manager_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByCreatedBy(User user);

    List<Task> findByAssignedTo(User user);
    
    Page<Task> findByCreatedBy(User user, Pageable pageable);
}