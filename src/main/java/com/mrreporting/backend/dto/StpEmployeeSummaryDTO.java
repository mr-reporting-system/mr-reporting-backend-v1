package com.mrreporting.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StpEmployeeSummaryDTO {

    private Long employeeId;
    private String employeeName;
    private String designationName;   // e.g. MR, ASM
    private String headquarter;       // District name of the employee's HQ
    private Long pendingRouteCount;   // How many STP routes are pending for this employee
}