package com.mrreporting.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "daily_allowances",
        uniqueConstraints = {
                // one DA rule per state + designation combination
                @UniqueConstraint(columnNames = {"state_id", "designation_id"})
        }
)
@Data
public class DailyAllowance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "state_id", nullable = false)
    private State state;

    @ManyToOne
    @JoinColumn(name = "designation_id", nullable = false)
    private Designation designation;

    // headquarter daily allowance
    @Column(name = "hq_da", nullable = false, precision = 10, scale = 2)
    private BigDecimal hqDa;

    // ex-headquarter daily allowance
    @Column(name = "ex_da", nullable = false, precision = 10, scale = 2)
    private BigDecimal exDa;

    // outstation daily allowance
    @Column(name = "out_da", nullable = false, precision = 10, scale = 2)
    private BigDecimal outDa;

    @Column(name = "mobile_allowance", precision = 10, scale = 2)
    private BigDecimal mobileAllowance = BigDecimal.ZERO;

    @Column(name = "net_allowance", precision = 10, scale = 2)
    private BigDecimal netAllowance = BigDecimal.ZERO;

    // stored as two separate columns — special allowance is calculated on the fly as their sum
    @Column(name = "postage_stationary", precision = 10, scale = 2)
    private BigDecimal postageStationary = BigDecimal.ZERO;

    @Column(name = "postage_freight", precision = 10, scale = 2)
    private BigDecimal postageFreight = BigDecimal.ZERO;

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