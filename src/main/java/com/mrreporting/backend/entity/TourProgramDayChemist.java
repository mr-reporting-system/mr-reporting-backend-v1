package com.mrreporting.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tour_program_day_chemists")
@Data
public class TourProgramDayChemist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_program_day_id", nullable = false)
    @JsonIgnore
    private TourProgramDay tourProgramDay;

    // provider with type = 'Chemist' or 'Stockist'
    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;
}