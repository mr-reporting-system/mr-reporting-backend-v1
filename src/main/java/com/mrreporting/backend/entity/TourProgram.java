package com.mrreporting.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "tour_programs",
        uniqueConstraints = {
                // one tour program per employee per month per year
                @UniqueConstraint(columnNames = {"employee_id", "month", "year"})
        }
)
@Data
public class TourProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    // month stored as integer 1-12
    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    private Integer year;

    // set to true when the MR submits their monthly plan
    @Column(name = "is_submitted")
    private Boolean isSubmitted = false;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    // set to true when admin approves the submitted plan
    @Column(name = "is_approved")
    private Boolean isApproved = false;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    // populated when admin rejects the plan
    @Column(name = "rejection_message", columnDefinition = "TEXT")
    private String rejectionMessage;

    @OneToMany(mappedBy = "tourProgram", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TourProgramDay> days = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}