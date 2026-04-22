package com.mrreporting.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class TargetReportRequestDTO {
    private Integer year;
    private List<Integer> stateIds;
    private List<Integer> districtIds;
    private List<Long> employeeIds;
}
