package com.mrreporting.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TargetSubmissionResponseDTO {
    private Integer year;
    private Long employeeCount;
    private Long savedTargetCount;
    private List<Long> employeeIds;
}
