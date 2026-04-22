package com.mrreporting.backend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class TargetModifyRequestDTO {
    private Long employeeId;
    private Integer year;
    private Map<Integer, BigDecimal> monthlyTargets;
}
