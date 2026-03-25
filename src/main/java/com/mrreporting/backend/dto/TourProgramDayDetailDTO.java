package com.mrreporting.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TourProgramDayDetailDTO {

    private LocalDate date;
    private String activityType;

    // each entry is "From : {area} -- To : {area}" built from the linked STP
    private List<String> approvedAreas;

    private String jointWorkWithPlan;

    // doctor names for display in the Approved Doctor column
    private List<String> approvedDoctors;

    // chemist/provider names for display in the Approved Chemist column
    private List<String> approvedChemists;

    private String remark;
}