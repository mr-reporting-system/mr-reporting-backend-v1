package com.mrreporting.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DcrDateWiseSummaryDTO {
    private Long adminWorkCount;
    private Long meetingCount;
    private Long holidayCount;
    private Long fieldWorkCount;
    private Long leaveCount;
    private Long lwpCount;
    private Long conferenceCount;
    private Long chemistWorkCount;
    private Long stockistWorkCount;
    private Long transitCount;
    private Long otherCount;
    private Long totalDates;
    private Long totalDoctorMet;
    private Long totalChemistMet;
    private Long totalStockistMet;
    private BigDecimal unlistedDoctorPob;
    private BigDecimal unlistedChemistPob;
    private BigDecimal unlistedStockistPob;
    private Long unlistedDoctorMet;
    private Long unlistedChemistMet;
    private Long unlistedStockistMet;
    private BigDecimal totalDoctorCallAverage;
    private BigDecimal totalChemistCallAverage;
    private BigDecimal totalStockistCallAverage;
    private BigDecimal totalUnlistedDoctorCallAverage;
    private BigDecimal totalUnlistedChemistCallAverage;
    private BigDecimal totalUnlistedStockistCallAverage;
    private Long tpDeviationCount;
    private Long jointWorkDays;
    private BigDecimal jointWorkCallAverage;
    private BigDecimal doctorPobValue;
    private BigDecimal chemistPobValue;
    private BigDecimal stockistPobValue;
    private BigDecimal combinedDoctorChemistPobValue;
    private BigDecimal totalPobValue;
}
