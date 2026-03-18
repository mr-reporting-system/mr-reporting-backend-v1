package com.mrreporting.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "designations")
@Data
public class Designation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Maps the frontend JSON key "name" to the DB column "designation_name"
    @Column(name = "designation_name", nullable = false, unique = true)
    private String name;

    // Maps the frontend JSON key "level" to the DB column "designation_level"
    @Column(name = "designation_level", nullable = false)
    private Integer level;

    // Maps the frontend JSON key "fullForm" to the DB column "designation_full_form"
    @Column(name = "designation_full_form")
    private String fullForm;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Automatically sets the timestamps when a new designation is created
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Automatically updates the timestamp if the designation is edited later
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}