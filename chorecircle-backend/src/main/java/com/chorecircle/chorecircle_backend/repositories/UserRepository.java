package com.chorecircle.chorecircle_backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chorecircle.chorecircle_backend.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    
    boolean existsByUsername(String username);
    
    @Modifying
    @Query("UPDATE User u SET u.password = :password WHERE u.id = :userId")
    void updatePassword(@Param("userId") Long userId, @Param("password") String password);
    
    @Modifying
    @Query("UPDATE User u SET u.enabled = :enabled WHERE u.id = :userId")
    void updateEnabledStatus(@Param("userId") Long userId, @Param("enabled") boolean enabled);
    
    @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword%")
    List<User> findByUsernameContaining(@Param("keyword") String keyword);
    
    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRolesName(@Param("roleName") String roleName);
    
    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r.id = :roleId")
    List<User> findByRolesId(@Param("roleId") Long roleId);
}
