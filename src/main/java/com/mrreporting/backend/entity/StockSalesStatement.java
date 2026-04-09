package com.mrreporting.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "stock_sales_statements",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"stockist_id", "statement_month", "statement_year"})
        }
)
@Data
public class StockSalesStatement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // snapshot geography / ownership at the time of statement creation
    @ManyToOne
    @JoinColumn(name = "state_id", nullable = false)
    private State state;

    @ManyToOne
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "stockist_id", nullable = false)
    private Provider stockist;

    @Column(name = "statement_month", nullable = false)
    private Integer month;

    @Column(name = "statement_year", nullable = false)
    private Integer year;

    @OneToMany(mappedBy = "statement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StockSalesStatementItem> items = new ArrayList<>();

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

    public void addItem(StockSalesStatementItem item) {
        items.add(item);
        item.setStatement(this);
    }
}
