package com.mrreporting.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "dcr_report_calls",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"dcr_report_id", "call_type", "party_name", "call_time"})
        }
)
@Data
public class DcrReportCall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dcr_report_id", nullable = false)
    private DcrReport dcrReport;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "provider_id")
    private Provider provider;

    @Column(name = "call_type", nullable = false, length = 20)
    private String callType;

    @Column(name = "is_listed", nullable = false)
    private Boolean isListed = true;

    @Column(name = "party_name", nullable = false, length = 255)
    private String partyName;

    @Column(name = "is_in_person", nullable = false)
    private Boolean isInPerson = true;

    @Column(name = "pob_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal pobAmount = BigDecimal.ZERO;

    @Column(name = "call_time")
    private LocalDateTime callTime;

    @Column(columnDefinition = "TEXT")
    private String remark;

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
