package com.mrreporting.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockistMappingReportRowDTO {
    private Long mappingId;

    private Integer employeeStateId;
    private String employeeStateName;

    private Integer employeeDistrictId;
    private String employeeDistrictName;

    private Long employeeId;
    private String userName;
    private String designationName;

    private Long stockistId;
    private String stockistCode;
    private String stockistName;

    private Integer stockistStateId;
    private String stockistStateName;

    private Integer stockistDistrictId;
    private String stockistDistrictName;
}
