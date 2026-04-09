package com.mrreporting.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class SSSViewFilterDTO {

    private List<Integer> stateIds;
    private List<Integer> districtIds;
    private List<Long> employeeIds;
    private List<Long> providerIds;
    private List<Long> stockistIds;

    private Integer month; // 1-12
    private Integer year;

    public List<Long> getResolvedProviderIds() {
        if (providerIds != null && !providerIds.isEmpty()) {
            return providerIds;
        }
        if (stockistIds != null && !stockistIds.isEmpty()) {
            return stockistIds;
        }
        return List.of();
    }
}
