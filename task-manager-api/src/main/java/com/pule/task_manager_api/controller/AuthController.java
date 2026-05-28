package com.pule.task_manager_api.controller;

import com.pule.task_manager_api.service.UserService;
import org.springframework.web.bind.annotation.*;

import com.pule.task_manager_api.dto.AuthResponse;
import com.pule.task_manager_api.dto.LoginRequest;
import com.pule.task_manager_api.dto.RegisterRequest;
import com.pule.task_manager_api.dto.UserResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

  @PostMapping("/register")
    public UserResponse register(@RequestBody RegisterRequest request) {
    return userService.registerUser(request);
}

@PostMapping("/login")
public AuthResponse login(@RequestBody LoginRequest request) {
    return userService.loginUser(request);
}

}