package com.chorecircle.chorecircle_backend.controllers;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chorecircle.chorecircle_backend.entities.Role;
import com.chorecircle.chorecircle_backend.entities.User;
import com.chorecircle.chorecircle_backend.services.UserService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Create a new user
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@RequestBody Map<String, Object> request) {
        try {
            String username = (String) request.get("username");
            String password = (String) request.get("password");
            @SuppressWarnings("unchecked")
            Set<String> roles = (Set<String>) request.get("roles");

            User user = userService.createUser(username, password, roles);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Get user by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(user))
                .orElse(ResponseEntity.notFound().build());
    }

    // Get user by username
    @GetMapping("/username/{username}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username)
                .map(user -> ResponseEntity.ok(user))
                .orElse(ResponseEntity.notFound().build());
    }

    // Get all users
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Search users by username
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String keyword) {
        List<User> users = userService.searchUsersByUsername(keyword);
        return ResponseEntity.ok(users);
    }

    // Update user password
    @PutMapping("/{id}/password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updatePassword(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String newPassword = request.get("password");
        boolean updated = userService.updatePassword(id, newPassword);
        
        if (updated) {
            return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Update user password by username
    @PutMapping("/username/{username}/password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updatePasswordByUsername(@PathVariable String username, @RequestBody Map<String, String> request) {
        String newPassword = request.get("password");
        boolean updated = userService.updatePasswordByUsername(username, newPassword);
        
        if (updated) {
            return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Update user roles
    @PutMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUserRoles(@PathVariable Long id, @RequestBody Map<String, Set<String>> request) {
        try {
            Set<String> roleNames = request.get("roles");
            boolean updated = userService.updateUserRoles(id, roleNames);
            
            if (updated) {
                return ResponseEntity.ok(Map.of("message", "Roles updated successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Add role to user
    @PostMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addRoleToUser(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String roleName = request.get("role");
        boolean added = userService.addRoleToUser(id, roleName);
        
        if (added) {
            return ResponseEntity.ok(Map.of("message", "Role added successfully"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "User or role not found"));
        }
    }

    // Remove role from user
    @DeleteMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> removeRoleFromUser(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String roleName = request.get("role");
        boolean removed = userService.removeRoleFromUser(id, roleName);
        
        if (removed) {
            return ResponseEntity.ok(Map.of("message", "Role removed successfully"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "User or role not found"));
        }
    }

    // Enable/disable user
    @PutMapping("/{id}/enabled")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUserEnabledStatus(@PathVariable Long id, @RequestBody Map<String, Boolean> request) {
        Boolean enabled = request.get("enabled");
        boolean updated = userService.updateUserEnabledStatus(id, enabled);
        
        if (updated) {
            return ResponseEntity.ok(Map.of("message", "User status updated successfully"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete user by ID
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUser(id);
        
        if (deleted) {
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete user by username
    @DeleteMapping("/username/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUserByUsername(@PathVariable String username) {
        boolean deleted = userService.deleteUserByUsername(username);
        
        if (deleted) {
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Check if user exists
    @GetMapping("/exists/{username}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Map<String, Boolean>> userExists(@PathVariable String username) {
        boolean exists = userService.userExists(username);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    // Get users by role
    @GetMapping("/by-role/{roleName}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable String roleName) {
        List<User> users = userService.getUsersByRole(roleName);
        return ResponseEntity.ok(users);
    }

    // Get users by role ID
    @GetMapping("/by-role-id/{roleId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<List<User>> getUsersByRoleId(@PathVariable Long roleId) {
        List<User> users = userService.getUsersByRoleId(roleId);
        return ResponseEntity.ok(users);
    }

    // Check if user has specific role
    @GetMapping("/{userId}/has-role/{roleName}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Map<String, Boolean>> userHasRole(@PathVariable Long userId, @PathVariable String roleName) {
        boolean hasRole = userService.userHasRole(userId, roleName);
        return ResponseEntity.ok(Map.of("hasRole", hasRole));
    }

    // Check if user has specific role by username
    @GetMapping("/username/{username}/has-role/{roleName}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Map<String, Boolean>> userHasRoleByUsername(@PathVariable String username, @PathVariable String roleName) {
        boolean hasRole = userService.userHasRoleByUsername(username, roleName);
        return ResponseEntity.ok(Map.of("hasRole", hasRole));
    }

    // Get user roles
    @GetMapping("/{userId}/roles")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Set<Role>> getUserRoles(@PathVariable Long userId) {
        Set<Role> roles = userService.getUserRoles(userId);
        return ResponseEntity.ok(roles);
    }

    // Get user roles by username
    @GetMapping("/username/{username}/roles")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Set<Role>> getUserRolesByUsername(@PathVariable String username) {
        Set<Role> roles = userService.getUserRolesByUsername(username);
        return ResponseEntity.ok(roles);
    }
} 