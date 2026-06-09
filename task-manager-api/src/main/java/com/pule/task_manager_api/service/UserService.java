package com.pule.task_manager_api.service;

import com.pule.task_manager_api.entity.User;
import com.pule.task_manager_api.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.pule.task_manager_api.dto.RegisterRequest;
import com.pule.task_manager_api.dto.UserResponse;
import com.pule.task_manager_api.dto.LoginRequest;
import com.pule.task_manager_api.dto.AuthResponse;
import com.pule.task_manager_api.entity.Role;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,JwtService jwtService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;

}

   

public UserResponse registerUser(RegisterRequest request) {

    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
        throw new RuntimeException("Email already exists");
    }

    // Convert DTO → Entity
    User user = new User();
    user.setName(request.getName());
    user.setEmail(request.getEmail());
    user.setRole(request.getRole());

    // Hash password
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    
    //set user role(Default=User)
    user.setRole(Role.USER);

    User savedUser = userRepository.save(user);

    // Convert Entity → Response DTO
    UserResponse response = new UserResponse();
    response.setId(savedUser.getId());
    response.setName(savedUser.getName());
    response.setEmail(savedUser.getEmail());
    response.setRole(savedUser.getRole());

    return response;
}

 
public AuthResponse loginUser(LoginRequest request) {

    User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        throw new RuntimeException("Invalid credentials");
    }

    // Generate token
    String token = jwtService.generateToken(user.getEmail());

    // Build response
    UserResponse userResponse = new UserResponse();
    userResponse.setId(user.getId());
    userResponse.setName(user.getName());
    userResponse.setEmail(user.getEmail());
    userResponse.setRole(user.getRole());

    AuthResponse response = new AuthResponse();
    response.setToken(token);
    response.setUser(userResponse);

    return response;
}

}