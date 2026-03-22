package com.mrreporting.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "doctors")
@Data
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Relationships ---
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

    // --- Core Doctor Details ---
    @Column(name = "doctor_code")
    private String doctorCode;

    @Column(name = "doctor_name", nullable = false)
    private String doctorName;

    @Column(name = "doctor_msl_no", nullable = false, unique = true)
    private String mslNo;

    private String category;
    private String degree;
    private String specialization;

    @Column(nullable = false, unique = true)
    private String phone;

    private String gender;
    private String address;

    @Column(name = "licence_no")
    private String licenceNo;

    @Column(name = "aadhaar_no")
    private String aadhaarNo;

    private String email;

    @Column(name = "frequency_visit")
    private Integer frequencyVisit = 0;

    // --- 🚦 NEW: Approval Tracking Fields ---
    @Column(name = "is_active")
    private Boolean isActive = false; // Defaults to false for new requests

    @Column(name = "request_status")
    private String requestStatus = "ADDITION"; // Defaults to Addition

    // --- Family & Visit Data ---
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DoctorChild> children = new ArrayList<>();

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DoctorVisitLocation> locations = new ArrayList<>();

    // --- Audit Trail ---
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Helper Methods
    public void addChild(DoctorChild child) {
        children.add(child);
        child.setDoctor(this);
    }

    public void addLocation(DoctorVisitLocation location) {
        locations.add(location);
        location.setDoctor(this);
    }
}