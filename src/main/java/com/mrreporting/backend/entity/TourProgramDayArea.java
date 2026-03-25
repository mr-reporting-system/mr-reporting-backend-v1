package com.mrreporting.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tour_program_day_areas")
@Data
public class TourProgramDayArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_program_day_id", nullable = false)
    @JsonIgnore
    private TourProgramDay tourProgramDay;

    // links to the approved STP route for this day.
    // the from_area and to_area names are read from the stp when rendering the detail view.
    @ManyToOne
    @JoinColumn(name = "stp_id", nullable = false)
    private Stp stp;
}