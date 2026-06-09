package com.pule.task_manager_api.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "Protected route working";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin-test")
    public String adminOnly() {

        return "You are admin";
}
}
