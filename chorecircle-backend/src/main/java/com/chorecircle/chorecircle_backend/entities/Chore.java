package com.chorecircle.chorecircle_backend.entities;

import java.time.LocalDate;

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
@Table(name = "chores")
@Data @NoArgsConstructor @AllArgsConstructor
public class Chore {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @ManyToOne
    private Workspace workspace;

    @ManyToOne
    private User assignedTo;

    private LocalDate dueDate;
    private boolean completed = false;
}
