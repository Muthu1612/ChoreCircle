package com.chorecircle.chorecircle_backend.entities;

import java.time.LocalDateTime;

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
@Table(name = "chore_history")
@Data @NoArgsConstructor @AllArgsConstructor
public class ChoreHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false)
    private Chore chore;

    @ManyToOne
    private User performedBy; // who completed it

    private LocalDateTime performedAt;

    private String note; // optional
}
