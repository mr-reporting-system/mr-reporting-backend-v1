package com.mrreporting.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

// request body sent when admin clicks "Create Daily Allowance".
// stateIds x designationIds creates that many records — same cross-product logic as FRC.
@Data
public class DailyAllowanceCreateDTO {

    private List<Integer> stateIds;
    private List<Long> designationIds;

    private BigDecimal hqDa;
    private BigDecimal exDa;
    private BigDecimal outDa;
    private BigDecimal mobileAllowance;
    private BigDecimal netAllowance;
    private BigDecimal postageStationary;
    private BigDecimal postageFreight;
}