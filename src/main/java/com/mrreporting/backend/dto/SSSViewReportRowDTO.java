package com.mrreporting.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SSSViewReportRowDTO {

    private Long providerId;
    private String partyName;
    private String providerType;

    private Long productId;
    private String productName;
    private String productCode;

    private BigDecimal netRate;

    private Integer opening;
    private BigDecimal openingValue;

    private Integer receipt;

    private Integer primarySale;
    private BigDecimal primaryValue;

    private Integer scheme;
    private Integer total;

    private Integer salesReturn;
    private BigDecimal returnValue;

    private Integer closing;
    private BigDecimal closingValue;

    private Integer expiry;
    private BigDecimal expiryValue;

    private Integer breakage;
    private BigDecimal breakageValue;

    private Integer batchRecall;
    private BigDecimal batchRecallValue;

    private String batchNumber;

    private Integer secondarySale;
    private BigDecimal secondarySaleValue;
}
