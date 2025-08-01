package com.chorecircle.chorecircle_backend.config;

import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.chorecircle.chorecircle_backend.services.RoleService;
import com.chorecircle.chorecircle_backend.services.UserService;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleService roleService;
    private final UserService userService;

    public DataInitializer(RoleService roleService, UserService userService) {
        this.roleService = roleService;
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Initialize default roles
        initializeDefaultRoles();
        
        // Initialize admin user if it doesn't exist
        initializeAdminUser();
    }

    private void initializeDefaultRoles() {
        try {
            roleService.initializeDefaultRoles();
            System.out.println("Default roles initialized successfully");
        } catch (Exception e) {
            System.out.println("Error initializing default roles: " + e.getMessage());
        }
    }

    private void initializeAdminUser() {
        try {
            String adminUsername = "admin";
            String adminPassword = "admin";

            if (!userService.userExists(adminUsername)) {
                userService.createUser(adminUsername, adminPassword, Set.of("ADMIN"));
                System.out.println("Initialized admin user successfully");
            } else {
                System.out.println("Admin user already exists");
            }
        } catch (Exception e) {
            System.out.println("Error creating admin user: " + e.getMessage());
        }
    }
} 