package com.mrreporting.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TourProgramSummaryDTO {

    // used by frontend to navigate to the detail view when clicking "Yes"
    private Long tourProgramId;

    private Long employeeId;
    private String employeeName;
    private String employeeCode;
    private String designation;
    private String stateName;
    private String headquarterName;

    private Boolean isSubmitted;
    private LocalDateTime submittedAt;

    private Boolean isApproved;
    private LocalDateTime approvedAt;

    private String rejectionMessage;
}