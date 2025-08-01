package com.chorecircle.chorecircle_backend.controllers;

import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chorecircle.chorecircle_backend.entities.User;
import com.chorecircle.chorecircle_backend.security.JwtTokenProvider;
import com.chorecircle.chorecircle_backend.services.UserService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, 
                         JwtTokenProvider tokenProvider, 
                         UserService userService) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody Map<String, String> loginRequest) {
        try {
            String username = loginRequest.get("username");
            String password = loginRequest.get("password");
            System.out.println("Username: " + username);
            System.out.println("Password: " + password);
            System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);
            
            System.out.println("JWT Token generated: " + jwt);
            System.out.println("Authentication successful for user: " + username);

            Map<String, Object> response = Map.of(
                "token", jwt,
                "type", "Bearer",
                "message", "Login successful"
            );
            
            System.out.println("Sending response: " + response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("Authentication failed: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid username or password"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> registerRequest) {
        try {
            String username = registerRequest.get("username");
            String password = registerRequest.get("password");

            if (userService.userExists(username)) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Username already exists"));
            }

            User user = userService.createUser(username, password);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                    "message", "User registered successfully",
                    "userId", user.getId(),
                    "username", user.getUsername()
                ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/register-admin")
    public ResponseEntity<?> registerAdmin(@RequestBody Map<String, String> registerRequest) {
        try {
            String username = registerRequest.get("username");
            String password = registerRequest.get("password");

            if (userService.userExists(username)) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Username already exists"));
            }

            User user = userService.createUser(username, password, Set.of("ADMIN"));
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                    "message", "Admin user registered successfully",
                    "userId", user.getId(),
                    "username", user.getUsername()
                ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/test")
    public ResponseEntity<?> testEndpoint() {
        return ResponseEntity.ok(Map.of(
            "message", "Test endpoint working",
            "timestamp", System.currentTimeMillis()
        ));
    }
} 