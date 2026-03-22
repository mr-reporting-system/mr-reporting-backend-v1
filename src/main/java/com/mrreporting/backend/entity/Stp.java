package com.mrreporting.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stps")
@Data
public class Stp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔗 Foreign Keys linking to your existing tables
    @ManyToOne
    @JoinColumn(name = "designation_id")
    private Designation designation;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    // Assuming your area entity is named Area
    @ManyToOne
    @JoinColumn(name = "from_area_id")
    private Area fromArea;

    @ManyToOne
    @JoinColumn(name = "to_area_id")
    private Area toArea;

    // 📝 Standard Columns
    @Column(name = "area_type")
    private String areaType;

    private Integer frc;

    // Using BigDecimal for decimal values to ensure precision
    private BigDecimal distance;

    @Column(name = "frequency_visit")
    private Integer frequencyVisit;

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