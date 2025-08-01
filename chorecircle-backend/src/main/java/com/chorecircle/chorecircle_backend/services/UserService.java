package com.chorecircle.chorecircle_backend.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chorecircle.chorecircle_backend.entities.Role;
import com.chorecircle.chorecircle_backend.entities.User;
import com.chorecircle.chorecircle_backend.repositories.UserRepository;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    // Create a new user
    public User createUser(String username, String password, Set<String> roleNames) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists: " + username);
        }

        User user = new User(username, passwordEncoder.encode(password));
        
        // Add roles to user
        if (roleNames != null && !roleNames.isEmpty()) {
            for (String roleName : roleNames) {
                Optional<Role> role = roleService.getRoleByName(roleName);
                if (role.isPresent()) {
                    user.addRole(role.get());
                } else {
                    throw new RuntimeException("Role not found: " + roleName);
                }
            }
        }

        return userRepository.save(user);
    }

    // Create a new user with default role
    public User createUser(String username, String password) {
        return createUser(username, password, Set.of("USER"));
    }

    // Get user by ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Get user by username
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Search users by username
    public List<User> searchUsersByUsername(String keyword) {
        return userRepository.findByUsernameContaining(keyword);
    }

    // Update user password
    public boolean updatePassword(Long userId, String newPassword) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            userRepository.updatePassword(userId, passwordEncoder.encode(newPassword));
            return true;
        }
        return false;
    }

    // Update user password by username
    public boolean updatePasswordByUsername(String username, String newPassword) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            userRepository.updatePassword(userOpt.get().getId(), passwordEncoder.encode(newPassword));
            return true;
        }
        return false;
    }

    // Update user roles
    public boolean updateUserRoles(Long userId, Set<String> roleNames) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.getRoles().clear(); // Remove existing roles
            
            // Add new roles
            for (String roleName : roleNames) {
                Optional<Role> role = roleService.getRoleByName(roleName);
                if (role.isPresent()) {
                    user.addRole(role.get());
                } else {
                    throw new RuntimeException("Role not found: " + roleName);
                }
            }
            
            userRepository.save(user);
            return true;
        }
        return false;
    }

    // Add role to user
    public boolean addRoleToUser(Long userId, String roleName) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Role> roleOpt = roleService.getRoleByName(roleName);
        
        if (userOpt.isPresent() && roleOpt.isPresent()) {
            User user = userOpt.get();
            user.addRole(roleOpt.get());
            userRepository.save(user);
            return true;
        }
        return false;
    }

    // Remove role from user
    public boolean removeRoleFromUser(Long userId, String roleName) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Role> roleOpt = roleService.getRoleByName(roleName);
        
        if (userOpt.isPresent() && roleOpt.isPresent()) {
            User user = userOpt.get();
            user.removeRole(roleOpt.get());
            userRepository.save(user);
            return true;
        }
        return false;
    }

    // Enable/disable user
    public boolean updateUserEnabledStatus(Long userId, boolean enabled) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            userRepository.updateEnabledStatus(userId, enabled);
            return true;
        }
        return false;
    }

    // Delete user by ID
    public boolean deleteUser(Long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            return true;
        }
        return false;
    }

    // Delete user by username
    public boolean deleteUserByUsername(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            userRepository.delete(userOpt.get());
            return true;
        }
        return false;
    }

    // Check if user exists
    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }

    // Check if user exists by ID
    public boolean userExists(Long userId) {
        return userRepository.existsById(userId);
    }

    // Get users by role
    public List<User> getUsersByRole(String roleName) {
        return userRepository.findByRolesName(roleName);
    }

    // Get users by role ID
    public List<User> getUsersByRoleId(Long roleId) {
        return userRepository.findByRolesId(roleId);
    }

    // Check if user has specific role
    public boolean userHasRole(Long userId, String roleName) {
        Optional<User> userOpt = getUserById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return user.getRoles().stream()
                    .anyMatch(role -> role.getName().equals(roleName));
        }
        return false;
    }

    // Check if user has specific role by username
    public boolean userHasRoleByUsername(String username, String roleName) {
        Optional<User> userOpt = getUserByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return user.getRoles().stream()
                    .anyMatch(role -> role.getName().equals(roleName));
        }
        return false;
    }

    // Get user roles
    public Set<Role> getUserRoles(Long userId) {
        Optional<User> userOpt = getUserById(userId);
        if (userOpt.isPresent()) {
            return userOpt.get().getRoles();
        }
        return Set.of();
    }

    // Get user roles by username
    public Set<Role> getUserRolesByUsername(String username) {
        Optional<User> userOpt = getUserByUsername(username);
        if (userOpt.isPresent()) {
            return userOpt.get().getRoles();
        }
        return Set.of();
    }
} 