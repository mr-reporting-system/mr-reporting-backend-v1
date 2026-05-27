package com.mrreporting.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DcrSubmitResponseDTO {
    private Long dcrId;
    private Long employeeId;
    private String employeeName;
    private LocalDate dcrDate;
    private String workingStatus;
    private Long areaCount;
    private Long doctorCallCount;
    private Long chemistStockistCallCount;
    private Long meetingCount;
    private Long expenseCount;
}
