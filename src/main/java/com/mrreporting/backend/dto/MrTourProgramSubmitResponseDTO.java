package com.mrreporting.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MrTourProgramSubmitResponseDTO {
    private Long tourProgramId;
    private Integer month;
    private Integer year;
    private Long updatedDayCount;
    private Boolean submitted;
    private Boolean approved;
}
