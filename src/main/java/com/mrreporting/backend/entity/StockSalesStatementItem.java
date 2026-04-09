package com.mrreporting.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "stock_sales_statement_items",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"statement_id", "product_id"})
        }
)
@Data
public class StockSalesStatementItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "statement_id", nullable = false)
    private StockSalesStatement statement;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // saved as a snapshot so old statements are not affected by later product price changes
    @Column(name = "net_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal netRate = BigDecimal.ZERO;

    @Column(name = "opening_qty")
    private Integer opening = 0;

    @Column(name = "receipt_qty")
    private Integer receipt = 0;

    @Column(name = "primary_sale_qty")
    private Integer primarySale = 0;

    @Column(name = "scheme_qty")
    private Integer scheme = 0;

    @Column(name = "sales_return_qty")
    private Integer salesReturn = 0;

    @Column(name = "closing_qty")
    private Integer closing = 0;

    @Column(name = "expiry_qty")
    private Integer expiry = 0;

    @Column(name = "breakage_qty")
    private Integer breakage = 0;

    @Column(name = "batch_recall_qty")
    private Integer batchRecall = 0;

    @Column(name = "batch_number")
    private String batchNumber;

    @Column(name = "secondary_sale_qty")
    private Integer secondarySale = 0;

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
