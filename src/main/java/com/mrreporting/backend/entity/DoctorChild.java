package com.mrreporting.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "doctor_children")
@Data
public class DoctorChild {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "child_name", nullable = false)
    private String childName;

    @Column(name = "child_age", nullable = false)
    private Integer childAge;

    // We use @ManyToOne to link back to the parent Doctor
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;
}