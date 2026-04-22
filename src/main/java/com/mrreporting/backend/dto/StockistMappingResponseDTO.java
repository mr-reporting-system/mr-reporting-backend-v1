package com.mrreporting.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockistMappingResponseDTO {
    private Long stockistId;
    private String stockistName;
    private Long mappedEmployeeCount;
    private List<Long> employeeIds;
}
