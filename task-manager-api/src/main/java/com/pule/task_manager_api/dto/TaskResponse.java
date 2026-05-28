package com.pule.task_manager_api.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TaskResponse {

    private Long id;

    private String title;

    private String description;

    private String status;

    private LocalDate dueDate;

    private String createdByEmail;

    private String assignedToEmail;
}