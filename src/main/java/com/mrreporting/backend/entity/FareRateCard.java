package com.mrreporting.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "fare_rate_cards")
@Data
public class FareRateCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // which designation this rule applies to
    @ManyToOne
    @JoinColumn(name = "designation_id", nullable = false)
    private Designation designation;

    @Column(name = "from_distance", nullable = false)
    private BigDecimal fromDistance;

    @Column(name = "to_distance", nullable = false)
    private BigDecimal toDistance;

    // "KM Wise" or "Lumsum"
    @Column(name = "allowance_type", nullable = false)
    private String allowanceType;

    // "hq", "ex", "out"
    @Column(name = "applicable_to", nullable = false)
    private String applicableTo;

    // monetary value — TA per KM for KM Wise, or flat amount for Lumsum
    @Column(name = "fare", nullable = false, precision = 10, scale = 2)
    private BigDecimal fare;

    // unique short code for quick lookup, e.g. "mr-hq", "mr-ex"
    @Column(name = "frc_code")
    private String frcCode;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}