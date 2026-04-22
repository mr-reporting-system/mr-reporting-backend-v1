package com.mrreporting.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DcrDateWiseRowDTO {
    private Long reportId;
    private LocalDate date;
    private String employeeName;
    private String managerName;
    private String workingStatus;
    private String reportedFrom;
    private LocalDateTime dayStart;
    private LocalDateTime dayEnd;
    private LocalDateTime firstCallTime;
    private LocalDateTime lastCallTime;
    private Boolean delayed;
    private Boolean deviated;
    private String jointWorkWith;
    private Long doctorMetCount;
    private Long chemistMetCount;
    private Long stockistMetCount;
    private BigDecimal totalPob;
}
