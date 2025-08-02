package com.chorecircle.chorecircle_backend.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.chorecircle.chorecircle_backend.services.RoleService;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "*")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    // Create a new role
    @PostMapping
    public ResponseEntity<?> createRole(@RequestBody Map<String, String> request) {
        try {
            String name = request.get("name");
            Role role = roleService.createRole(name);
            return ResponseEntity.status(HttpStatus.CREATED).body(role);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Get role by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getRoleById(@PathVariable Long id) {
        return roleService.getRoleById(id)
                .map(role -> ResponseEntity.ok(role))
                .orElse(ResponseEntity.notFound().build());
    }

    // Get role by name
    @GetMapping("/name/{name}")
    public ResponseEntity<?> getRoleByName(@PathVariable String name) {
        return roleService.getRoleByName(name)
                .map(role -> ResponseEntity.ok(role))
                .orElse(ResponseEntity.notFound().build());
    }

    // Get all roles
    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    // Get all roles ordered by name
    @GetMapping("/ordered")
    public ResponseEntity<List<Role>> getAllRolesOrderByName() {
        List<Role> roles = roleService.getAllRolesOrderByName();
        return ResponseEntity.ok(roles);
    }

    // Search roles by name
    @GetMapping("/search")
    public ResponseEntity<List<Role>> searchRoles(@RequestParam String keyword) {
        List<Role> roles = roleService.searchRolesByName(keyword);
        return ResponseEntity.ok(roles);
    }

    // Update role name by ID
    @PutMapping("/{id}/name")
    public ResponseEntity<?> updateRoleName(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String newName = request.get("name");
            boolean updated = roleService.updateRoleName(id, newName);
            
            if (updated) {
                return ResponseEntity.ok(Map.of("message", "Role name updated successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Update role name by current name
    @PutMapping("/name/{currentName}")
    public ResponseEntity<?> updateRoleNameByName(@PathVariable String currentName, @RequestBody Map<String, String> request) {
        try {
            String newName = request.get("name");
            boolean updated = roleService.updateRoleNameByName(currentName, newName);
            
            if (updated) {
                return ResponseEntity.ok(Map.of("message", "Role name updated successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Delete role by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable Long id) {
        try {
            boolean deleted = roleService.deleteRole(id);
            
            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "Role deleted successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Delete role by name
    @DeleteMapping("/name/{name}")
    public ResponseEntity<?> deleteRoleByName(@PathVariable String name) {
        try {
            boolean deleted = roleService.deleteRoleByName(name);
            
            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "Role deleted successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Force delete role by ID (removes from all users first)
    @DeleteMapping("/{id}/force")
    public ResponseEntity<?> forceDeleteRole(@PathVariable Long id) {
        try {
            boolean deleted = roleService.forceDeleteRole(id);
            
            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "Role force deleted successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Force delete role by name
    @DeleteMapping("/name/{name}/force")
    public ResponseEntity<?> forceDeleteRoleByName(@PathVariable String name) {
        try {
            boolean deleted = roleService.forceDeleteRoleByName(name);
            
            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "Role force deleted successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Check if role exists
    @GetMapping("/exists/{name}")
    public ResponseEntity<Map<String, Boolean>> roleExists(@PathVariable String name) {
        boolean exists = roleService.roleExists(name);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    // Get count of users with a specific role
    @GetMapping("/{id}/users-count")
    public ResponseEntity<Map<String, Long>> getUsersCountWithRole(@PathVariable Long id) {
        long count = roleService.getUsersCountWithRole(id);
        return ResponseEntity.ok(Map.of("count", count));
    }

    // Get count of users with a specific role by name
    @GetMapping("/name/{name}/users-count")
    public ResponseEntity<Map<String, Long>> getUsersCountWithRoleByName(@PathVariable String name) {
        long count = roleService.getUsersCountWithRoleByName(name);
        return ResponseEntity.ok(Map.of("count", count));
    }

    // // Initialize default roles
    // @PostMapping("/initialize")
    // public ResponseEntity<Map<String, String>> initializeDefaultRoles() {
    //     roleService.initializeDefaultRoles();
    //     return ResponseEntity.ok(Map.of("message", "Default roles initialized successfully"));
    // }
} 