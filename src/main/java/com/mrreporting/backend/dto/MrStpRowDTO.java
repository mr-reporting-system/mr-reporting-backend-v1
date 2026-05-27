package com.mrreporting.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MrStpRowDTO {
    private Long stpId;
    private String fromAreaName;
    private String toAreaName;
    private String areaType;
    private BigDecimal distance;
    private Integer frequencyVisit;
    private Boolean managerApproved;
    private Boolean adminApproved;
    private String requestStatus;
}
