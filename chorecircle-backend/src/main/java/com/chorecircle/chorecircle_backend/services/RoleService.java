package com.chorecircle.chorecircle_backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chorecircle.chorecircle_backend.entities.Role;
import com.chorecircle.chorecircle_backend.entities.User;
import com.chorecircle.chorecircle_backend.repositories.RoleRepository;
import com.chorecircle.chorecircle_backend.repositories.UserRepository;

@Service
@Transactional
public class RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public RoleService(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    // Create a new role
    public Role createRole(String name) {
        if (roleRepository.existsByName(name)) {
            throw new RuntimeException("Role already exists: " + name);
        }

        Role role = new Role(name);
        return roleRepository.save(role);
    }

    // Get role by ID
    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    // Get role by name
    public Optional<Role> getRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    // Get all roles
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    // Get all roles ordered by name
    public List<Role> getAllRolesOrderByName() {
        return roleRepository.findAllOrderByName();
    }

    // Search roles by name
    public List<Role> searchRolesByName(String keyword) {
        return roleRepository.findByNameContaining(keyword);
    }

    // Update role name
    public boolean updateRoleName(Long roleId, String newName) {
        Optional<Role> roleOpt = roleRepository.findById(roleId);
        if (roleOpt.isPresent()) {
            // Check if new name already exists
            if (roleRepository.existsByName(newName)) {
                throw new RuntimeException("Role name already exists: " + newName);
            }
            roleRepository.updateRoleName(roleId, newName);
            return true;
        }
        return false;
    }

    // Update role name by current name
    public boolean updateRoleNameByName(String currentName, String newName) {
        Optional<Role> roleOpt = roleRepository.findByName(currentName);
        if (roleOpt.isPresent()) {
            // Check if new name already exists
            if (roleRepository.existsByName(newName)) {
                throw new RuntimeException("Role name already exists: " + newName);
            }
            roleRepository.updateRoleName(roleOpt.get().getId(), newName);
            return true;
        }
        return false;
    }

    // Delete role by ID
    public boolean deleteRole(Long roleId) {
        Optional<Role> roleOpt = roleRepository.findById(roleId);
        if (roleOpt.isPresent()) {
            // Check if any users have this role
            long userCount = roleRepository.countUsersWithRole(roleId);
            if (userCount > 0) {
                throw new RuntimeException("Cannot delete role. " + userCount + " user(s) have this role.");
            }
            roleRepository.deleteById(roleId);
            return true;
        }
        return false;
    }

    // Delete role by name
    public boolean deleteRoleByName(String name) {
        Optional<Role> roleOpt = roleRepository.findByName(name);
        if (roleOpt.isPresent()) {
            // Check if any users have this role
            long userCount = roleRepository.countUsersWithRole(roleOpt.get().getId());
            if (userCount > 0) {
                throw new RuntimeException("Cannot delete role. " + userCount + " user(s) have this role.");
            }
            roleRepository.delete(roleOpt.get());
            return true;
        }
        return false;
    }

    // Force delete role (removes role from all users first)
    public boolean forceDeleteRole(Long roleId) {
        Optional<Role> roleOpt = roleRepository.findById(roleId);
        if (roleOpt.isPresent()) {
            Role role = roleOpt.get();
            // Remove this role from all users
            List<User> usersWithRole = userRepository.findByRolesId(roleId);
            usersWithRole.forEach(user -> user.removeRole(role));
            userRepository.saveAll(usersWithRole);
            roleRepository.deleteById(roleId);
            return true;
        }
        return false;
    }

    // Force delete role by name
    public boolean forceDeleteRoleByName(String name) {
        Optional<Role> roleOpt = roleRepository.findByName(name);
        if (roleOpt.isPresent()) {
            Role role = roleOpt.get();
            // Remove this role from all users
            List<User> usersWithRole = userRepository.findByRolesId(role.getId());
            usersWithRole.forEach(user -> user.removeRole(role));
            userRepository.saveAll(usersWithRole);
            roleRepository.delete(roleOpt.get());
            return true;
        }
        return false;
    }

    // Check if role exists
    public boolean roleExists(String name) {
        return roleRepository.existsByName(name);
    }

    // Check if role exists by ID
    public boolean roleExists(Long roleId) {
        return roleRepository.existsById(roleId);
    }

    // Get count of users with a specific role
    public long getUsersCountWithRole(Long roleId) {
        return roleRepository.countUsersWithRole(roleId);
    }

    // Get count of users with a specific role by name
    public long getUsersCountWithRoleByName(String roleName) {
        Optional<Role> roleOpt = roleRepository.findByName(roleName);
        if (roleOpt.isPresent()) {
            return roleRepository.countUsersWithRole(roleOpt.get().getId());
        }
        return 0;
    }

    // Initialize default roles if they don't exist
    public void initializeDefaultRoles() {
        String[] defaultRoles = {"USER", "ADMIN", "MODERATOR"};
        
        for (String roleName : defaultRoles) {
            if (!roleRepository.existsByName(roleName)) {
                createRole(roleName);
            }
        }
    }
} 