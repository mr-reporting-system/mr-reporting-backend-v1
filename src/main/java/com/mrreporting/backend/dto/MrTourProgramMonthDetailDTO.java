package com.mrreporting.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MrTourProgramMonthDetailDTO {
    private Long tourProgramId;
    private Integer month;
    private Integer year;
    private Boolean submitted;
    private LocalDateTime submittedAt;
    private Boolean approved;
    private LocalDateTime approvedAt;
    private String approvedByName;
    private String rejectionMessage;
    private List<MrTourProgramDayRowDTO> rows;
}
