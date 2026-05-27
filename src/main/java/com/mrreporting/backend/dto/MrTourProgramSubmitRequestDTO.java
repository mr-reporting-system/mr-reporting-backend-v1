package com.mrreporting.backend.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class MrTourProgramSubmitRequestDTO {
    private List<LocalDate> dates;
    private String activityType;
    private String remarks;
}
