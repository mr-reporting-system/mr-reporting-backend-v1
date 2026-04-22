package com.mrreporting.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class StockistMappingReportFilterDTO {
    private List<Integer> stateIds;
    private List<Integer> districtIds;
    private List<Long> employeeIds;
}
