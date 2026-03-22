package com.mrreporting.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApprovalSummaryDTO {
    private String stateName;
    private String districtName;
    private String employeeName;
    private Long employeeId;
    private Long totalRequests;
}