package com.mrreporting.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class StockistMappingRequestDTO {
    private Long stockistId;
    private List<Long> employeeIds;
}
