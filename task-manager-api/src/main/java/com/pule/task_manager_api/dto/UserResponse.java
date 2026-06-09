package com.pule.task_manager_api.dto;

import com.pule.task_manager_api.entity.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private Role role;
}