package com.mrreporting.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockistMappingStockistRowDTO {
    private Long stockistId;
    private String headquarterName;
    private String stockistName;
    private String ownerEmployeeName;
    private Long mappedEmployeeCount;
}
