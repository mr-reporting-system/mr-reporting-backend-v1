package com.mrreporting.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "areas")
@Data
public class Area {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Relationships 🗺️👔 ---
    @ManyToOne
    @JoinColumn(name = "state_id", nullable = false)
    private State state;

    @ManyToOne
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    // --- Core Area Details ✍️ ---
    @Column(name = "area_name", nullable = false)
    private String areaName;

    @Column(name = "area_code", nullable = false, unique = true)
    private String areaCode;

    @Column(name = "area_type", nullable = false)
    private String areaType;

    // --- Internal Business & Counter Logic 📈 ---
    @Column(name = "status")
    private Boolean status = true;

    @Column(name = "total_doctor")
    private Integer totalDoctor = 0;

    @Column(name = "total_chemist")
    private Integer totalChemist = 0;

    @Column(name = "total_stockist")
    private Integer totalStockist = 0;

    // --- Audit Trail 🕰️ ---
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}