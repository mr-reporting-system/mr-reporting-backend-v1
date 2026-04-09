package com.mrreporting.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockSalesStatementRowResponseDTO {

    private Long productId;
    private String productCode;
    private String productName;
    private BigDecimal netRate;

    private Integer opening;
    private Integer receipt;
    private Integer primarySale;
    private Integer scheme;
    private Integer salesReturn;
    private Integer closing;
    private Integer expiry;
    private Integer breakage;
    private Integer batchRecall;
    private String batchNumber;
    private Integer secondarySale;
}
