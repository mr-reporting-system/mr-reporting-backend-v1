package com.mrreporting.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockistMappingEmployeeRowDTO {
    private Long employeeId;
    private String headquarterName;
    private String userName;
    private String designationName;
    private Boolean mapped;
}
