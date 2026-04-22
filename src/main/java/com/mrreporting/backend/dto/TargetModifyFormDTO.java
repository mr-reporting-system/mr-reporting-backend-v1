package com.mrreporting.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TargetModifyFormDTO {
    private Long employeeId;
    private String employeeName;
    private Integer year;
    private Map<Integer, BigDecimal> monthlyTargets;
}
