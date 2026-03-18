package com.mrreporting.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalTime;

@Entity
@Table(name = "doctor_visit_slots")
@Data
public class DoctorVisitSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "from_time", nullable = false)
    private LocalTime fromTime;

    @Column(name = "to_time", nullable = false)
    private LocalTime toTime;

    // This links to the Visit Location, not the Doctor directly
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private DoctorVisitLocation location;
}