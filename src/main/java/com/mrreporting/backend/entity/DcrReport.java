package com.mrreporting.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "dcr_reports",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"employee_id", "report_date"})
        }
)
@Data
public class DcrReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "tour_program_day_id")
    private TourProgramDay tourProgramDay;

    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;

    @Column(name = "working_status", nullable = false, length = 50)
    private String workingStatus;

    @Column(name = "reported_from", nullable = false, length = 30)
    private String reportedFrom = "MOB";

    @Column(name = "day_start_time")
    private LocalDateTime dayStartTime;

    @Column(name = "day_end_time")
    private LocalDateTime dayEndTime;

    @Column(name = "first_call_time")
    private LocalDateTime firstCallTime;

    @Column(name = "last_call_time")
    private LocalDateTime lastCallTime;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "is_delayed")
    private Boolean isDelayed = false;

    @Column(name = "is_deviated")
    private Boolean isDeviated = false;

    @Column(name = "joint_work_with", length = 120)
    private String jointWorkWith;

    @Column(columnDefinition = "TEXT")
    private String remark;

    @OneToMany(mappedBy = "dcrReport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DcrReportCall> calls = new ArrayList<>();

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
