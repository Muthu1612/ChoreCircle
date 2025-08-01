package com.chorecircle.chorecircle_backend.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.chorecircle.chorecircle_backend.services.UserService;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private final UserService userService;

    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Loading user by username: {}", username);
        
        return userService.getUserByUsername(username)
                .map(user -> {
                    logger.debug("Found user: {} with roles: {}", user.getUsername(), user.getRoles());
                    return new CustomUserDetails(user);
                })
                .orElseThrow(() -> {
                    logger.debug("User not found: {}", username);
                    return new UsernameNotFoundException("User not found: " + username);
                });
    }
} 