package com.mrreporting.backend.dto;

import lombok.Data;

@Data
public class StockSalesStatementRowRequestDTO {

    private Long productId;

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
