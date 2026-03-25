package com.mrreporting.backend.dto;

import lombok.Data;
import java.math.BigDecimal;

// represents a single rule row in the dynamic form.
// the admin can add multiple of these before clicking "Create FRC".
@Data
public class FareRateCardRowDTO {

    private BigDecimal fromDistance;
    private BigDecimal toDistance;
    private String allowanceType;   // "KM Wise" or "Lumsum"
    private String applicableTo;    // "hq", "ex", "out"
    private BigDecimal fare;
    private String frcCode;
    private String description;
}