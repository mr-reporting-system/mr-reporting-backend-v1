package com.mrreporting.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Set;

@Data
public class ProductRequestDTO {
    // Product Information (Strings)
    private String productName;
    private String productCode;
    private String productType;
    private String productShortName;
    private String packageSize;
    private String samplePackageSize;

    // Product Rate Information (BigDecimals to match numeric(38,2))
    private BigDecimal ptw;
    private BigDecimal ptr;
    private BigDecimal mrp;
    private BigDecimal sampleRate;

    // Logic field (Boolean to match boolean)
    private BigDecimal includeVat;

    // Relationship mapping
    private Set<Integer> stateIds;
}