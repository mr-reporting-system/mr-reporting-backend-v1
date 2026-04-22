package com.mrreporting.backend.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class DcrConsolidateFilterDTO {
    private String filterMode;
    private String scopeType;
    private List<Integer> stateIds;
    private List<Integer> districtIds;
    private String status;
    private LocalDate fromDate;
    private LocalDate toDate;
}
