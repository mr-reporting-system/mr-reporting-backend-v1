package com.mrreporting.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tour_program_days")
@Data
public class TourProgramDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_program_id", nullable = false)
    @JsonIgnore
    private TourProgram tourProgram;

    // the specific calendar date this day entry represents
    @Column(nullable = false)
    private LocalDate date;

    // Working, Holiday, Leave, etc.
    @Column(name = "activity_type")
    private String activityType;

    // joint work with plan field shown in the detail table
    @Column(name = "joint_work_with_plan")
    private String jointWorkWithPlan;

    // the remark shown in the last column of the detail view
    private String remark;

    // STP routes planned for this day
    @OneToMany(mappedBy = "tourProgramDay", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TourProgramDayArea> areas = new ArrayList<>();

    // doctors planned for this day
    @OneToMany(mappedBy = "tourProgramDay", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TourProgramDayDoctor> doctors = new ArrayList<>();

    // chemists/providers planned for this day
    @OneToMany(mappedBy = "tourProgramDay", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TourProgramDayChemist> chemists = new ArrayList<>();
}