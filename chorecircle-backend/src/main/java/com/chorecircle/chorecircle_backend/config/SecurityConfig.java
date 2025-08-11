package com.chorecircle.chorecircle_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.chorecircle.chorecircle_backend.security.CustomUserDetailsService;
import com.chorecircle.chorecircle_backend.security.JwtAuthenticationEntryPoint;
import com.chorecircle.chorecircle_backend.security.JwtAuthenticationFilter;
import com.chorecircle.chorecircle_backend.security.JwtTokenProvider;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    public SecurityConfig(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter,
                                         CustomUserDetailsService customUserDetailsService, 
                                         PasswordEncoder passwordEncoder) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.and())
            .authorizeHttpRequests(authz -> authz
                // Public endpoints - no authentication required
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                
                // Admin and Moderator only endpoints - User management (read operations)
                .requestMatchers("GET", "/api/users").hasAnyRole("ADMIN", "MODERATOR") // GET all users
                .requestMatchers("GET", "/api/users/{id}").hasAnyRole("ADMIN", "MODERATOR") // GET user by ID
                .requestMatchers("GET", "/api/users/username/{username}").hasAnyRole("ADMIN", "MODERATOR") // GET user by username
                .requestMatchers("GET", "/api/users/exists/**").hasAnyRole("ADMIN", "MODERATOR") // Check if user exists
                .requestMatchers("GET", "/api/users/search").hasAnyRole("ADMIN", "MODERATOR") // Search users
                .requestMatchers("GET", "/api/users/by-role/**").hasAnyRole("ADMIN", "MODERATOR") // Get users by role
                .requestMatchers("GET", "/api/users/{userId}/has-role/**").hasAnyRole("ADMIN", "MODERATOR") // Check user roles
                .requestMatchers("GET", "/api/users/{userId}/roles").hasAnyRole("ADMIN", "MODERATOR") // Get user roles
                .requestMatchers("GET", "/api/users/username/{username}/roles").hasAnyRole("ADMIN", "MODERATOR") // Get user roles by username
                
                // Admin only endpoints - User creation, modification, deletion
                .requestMatchers("POST", "/api/users").hasRole("ADMIN") // POST create user
                .requestMatchers("PUT", "/api/users/{id}/password").hasRole("ADMIN") // PUT update password
                .requestMatchers("PUT", "/api/users/username/{username}/password").hasRole("ADMIN") // PUT update password by username
                .requestMatchers("PUT", "/api/users/{id}/roles").hasRole("ADMIN") // PUT update user roles
                .requestMatchers("POST", "/api/users/{id}/roles").hasRole("ADMIN") // POST add role to user
                .requestMatchers("DELETE", "/api/users/{id}/roles").hasRole("ADMIN") // DELETE remove role from user
                .requestMatchers("PUT", "/api/users/{id}/enabled").hasRole("ADMIN") // PUT enable/disable user
                .requestMatchers("DELETE", "/api/users/{id}").hasRole("ADMIN") // DELETE delete user
                .requestMatchers("DELETE", "/api/users/username/{username}").hasRole("ADMIN") // DELETE delete user by username
                
                // Admin and Moderator only endpoints - Role management (read operations)
                .requestMatchers("GET", "/api/roles").hasAnyRole("ADMIN", "MODERATOR") // GET all roles
                .requestMatchers("GET", "/api/roles/{id}").hasAnyRole("ADMIN", "MODERATOR") // GET role by ID
                .requestMatchers("GET", "/api/roles/name/{name}").hasAnyRole("ADMIN", "MODERATOR") // GET role by name
                .requestMatchers("GET", "/api/roles/ordered").hasAnyRole("ADMIN", "MODERATOR") // GET all roles ordered
                .requestMatchers("GET", "/api/roles/search").hasAnyRole("ADMIN", "MODERATOR") // Search roles
                .requestMatchers("GET", "/api/roles/exists/{name}").hasAnyRole("ADMIN", "MODERATOR") // Check if role exists
                .requestMatchers("GET", "/api/roles/{id}/users-count").hasAnyRole("ADMIN", "MODERATOR") // Get users count with role
                .requestMatchers("GET", "/api/roles/name/{name}/users-count").hasAnyRole("ADMIN", "MODERATOR") // Get users count with role by name
                
                // Admin only endpoints - Role creation, modification, deletion
                .requestMatchers("POST", "/api/roles").hasRole("ADMIN") // POST create role
                .requestMatchers("PUT", "/api/roles/{id}/name").hasRole("ADMIN") // PUT update role name
                .requestMatchers("PUT", "/api/roles/name/{currentName}").hasRole("ADMIN") // PUT update role name by current name
                .requestMatchers("DELETE", "/api/roles/{id}").hasRole("ADMIN") // DELETE delete role
                .requestMatchers("DELETE", "/api/roles/name/{name}").hasRole("ADMIN") // DELETE delete role by name
                .requestMatchers("DELETE", "/api/roles/{id}/force").hasRole("ADMIN") // DELETE force delete role
                .requestMatchers("DELETE", "/api/roles/name/{name}/force").hasRole("ADMIN") // DELETE force delete role by name
                
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider(customUserDetailsService, passwordEncoder))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(CustomUserDetailsService customUserDetailsService, 
                                                           PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        authProvider.setHideUserNotFoundExceptions(false);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, 
                                                          CustomUserDetailsService customUserDetailsService) {
        return new JwtAuthenticationFilter(jwtTokenProvider, customUserDetailsService);
    }
} 