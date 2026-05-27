package com.mrreporting.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MrTourProgramDayRowDTO {
    private Long dayId;
    private LocalDate date;
    private String submittedActivity;
    private String approvedActivity;
    private List<String> submittedDoctors;
    private List<String> approvedDoctors;
    private List<String> submittedChemists;
    private List<String> approvedChemists;
    private String remark;
}
