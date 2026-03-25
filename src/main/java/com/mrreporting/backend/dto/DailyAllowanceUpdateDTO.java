package com.mrreporting.backend.dto;

import lombok.Data;
import java.math.BigDecimal;

// used when admin clicks the edit icon on a table row.
// state and designation cannot be changed on edit.
@Data
public class DailyAllowanceUpdateDTO {

    private BigDecimal hqDa;
    private BigDecimal exDa;
    private BigDecimal outDa;
    private BigDecimal mobileAllowance;
    private BigDecimal netAllowance;
    private BigDecimal postageStationary;
    private BigDecimal postageFreight;
}