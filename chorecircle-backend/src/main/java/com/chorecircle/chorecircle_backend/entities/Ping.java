package com.chorecircle.chorecircle_backend.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pings")
@Data @NoArgsConstructor @AllArgsConstructor
public class Ping {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Chore chore;

    @ManyToOne(optional = false)
    private User targetUser;

    @ManyToOne
    private User sentBy; // null for system-generated pings

    private LocalDateTime sentAt = LocalDateTime.now();

    @Column(length = 255)
    private String message;
}
