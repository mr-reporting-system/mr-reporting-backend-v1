package com.mrreporting.backend.dto;

import lombok.Data;
import java.math.BigDecimal;

// sent when admin edits a single row in the table.
// designation cannot be changed on edit — only the rule fields.
@Data
public class FareRateCardUpdateDTO {

    private BigDecimal fromDistance;
    private BigDecimal toDistance;
    private String allowanceType;
    private String applicableTo;
    private BigDecimal fare;
    private String frcCode;
    private String description;
}