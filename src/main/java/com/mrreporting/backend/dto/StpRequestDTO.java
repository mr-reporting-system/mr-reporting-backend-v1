package com.mrreporting.backend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class StpRequestDTO {
    private Long designationId;
    private Long employeeId;
    private Long fromAreaId;
    private Long toAreaId;
    private String areaType;
    private Integer frc;
    private BigDecimal distance;
    private Integer frequencyVisit;
}