package com.mrreporting.backend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class TargetSubmissionRequestDTO {
    private Integer year;
    private List<Long> employeeIds;
    private Map<Integer, BigDecimal> monthlyTargets;
}
