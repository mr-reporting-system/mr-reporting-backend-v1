package com.mrreporting.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "doctor_visit_locations")
@Data
public class DoctorVisitLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String city;

    @Column(name = "session_type")
    private String sessionType; // e.g., Morning, Evening

    // Link back to the parent Doctor
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    // Link down to the multiple Time Slots 🕒
    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DoctorVisitSlot> slots = new ArrayList<>();

    // Helper method to add a slot and keep both sides of the relationship in sync
    public void addSlot(DoctorVisitSlot slot) {
        slots.add(slot);
        slot.setLocation(this);
    }
}