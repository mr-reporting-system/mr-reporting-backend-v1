package com.mrreporting.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "products")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;
    private String productCode;
    private String productType;
    private String productShortName;
    private String packageSize;
    private String samplePackageSize;

    // 💰 Using BigDecimal for financial accuracy
    private BigDecimal ptw;
    private BigDecimal ptr;
    private BigDecimal mrp;
    private BigDecimal sampleRate;

    @Column(name = "include_vat")
    private BigDecimal includeVat;

    // 🤝 The Many-to-Many Relationship
    @ManyToMany
    @JoinTable(
            name = "product_states",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "state_id")
    )
    private Set<State> states;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}