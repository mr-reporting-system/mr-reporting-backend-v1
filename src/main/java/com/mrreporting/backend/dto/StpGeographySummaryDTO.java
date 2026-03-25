package com.mrreporting.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StpGeographySummaryDTO {

    private String stateName;
    private String headquarter;   // District name — the HQ column in Level 1 table
    private Integer districtId;   // Passed to Level 2 query when row is clicked
    private Long employeeCount;   // How many unique employees have pending STPs here
    private Long totalPending;    // Total pending STP routes in this HQ
}