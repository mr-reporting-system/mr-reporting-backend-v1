package com.mrreporting.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TargetReportRowDTO {
    private Long employeeId;
    private String employeeName;
    private Map<Integer, BigDecimal> monthlyTargets;
    private BigDecimal total;
}
