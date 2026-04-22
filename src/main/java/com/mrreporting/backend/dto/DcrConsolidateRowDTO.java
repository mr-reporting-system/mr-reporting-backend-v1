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
public class DcrConsolidateRowDTO {
    private Long employeeId;
    private String headquarterName;
    private String employeeCode;
    private String employeeName;
    private String managerName;
    private String designation;
    private LocalDate dateOfJoining;
    private LocalDate dateOfConfirmation;
    private String mobile;
    private LocalDateTime lastSubmittedDcr;
    private Long totalDcr;
    private Long inPersonDoctorCallCount;
    private BigDecimal doctorPob;
    private Long chemistMetCount;
    private Long stockistMetCount;
    private Long totalProviderMet;
    private BigDecimal totalPob;
}
