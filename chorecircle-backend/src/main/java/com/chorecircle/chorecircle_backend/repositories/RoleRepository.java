package com.chorecircle.chorecircle_backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chorecircle.chorecircle_backend.entities.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
    
    boolean existsByName(String name);
    
    @Query("SELECT r FROM Role r WHERE r.name LIKE %:keyword%")
    List<Role> findByNameContaining(@Param("keyword") String keyword);
    
    @Modifying
    @Query("UPDATE Role r SET r.name = :newName WHERE r.id = :roleId")
    void updateRoleName(@Param("roleId") Long roleId, @Param("newName") String newName);
    
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.id = :roleId")
    long countUsersWithRole(@Param("roleId") Long roleId);
    
    @Query("SELECT r FROM Role r ORDER BY r.name ASC")
    List<Role> findAllOrderByName();
} 