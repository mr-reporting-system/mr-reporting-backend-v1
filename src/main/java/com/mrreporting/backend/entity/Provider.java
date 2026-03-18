package com.mrreporting.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "providers")
@Data
public class Provider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Relationships (The "Where" and "Who") 🗺️ ---
    @ManyToOne
    @JoinColumn(name = "state_id", nullable = false)
    private State state;

    @ManyToOne
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "area_id", nullable = false)
    private Area area;

    // --- Core Provider Details ✍️ ---
    @Column(nullable = false)
    private String type; // 'Chemist' or 'Stockist'

    @Column(name = "provider_code")
    private String providerCode;

    @Column(name = "provider_name", nullable = false)
    private String providerName;

    private String phone;

    @Column(name = "aadhaar_no")
    private String aadhaarNo;

    // --- Additional Info (Other Info Section) 📋 ---
    @Column(name = "owner_name")
    private String ownerName;

    @Column(name = "owner_dob")
    private LocalDate ownerDob;

    @Column(name = "owner_doa")
    private LocalDate ownerDoa;

    @Column(name = "shop_doa")
    private LocalDate shopDoa;

    @Column(columnDefinition = "TEXT")
    private String address;

    private String city;
    private String email;

    @Column(name = "pan_card")
    private String panCard;

    @Column(name = "gst_number")
    private String gstNumber;

    @Column(name = "licence_no")
    private String licenceNo;

    private String category;

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