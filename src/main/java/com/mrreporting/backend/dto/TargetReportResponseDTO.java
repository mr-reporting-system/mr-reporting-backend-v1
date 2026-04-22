package com.mrreporting.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TargetReportResponseDTO {
    private Integer year;
    private List<TargetReportRowDTO> rows;
    private Map<Integer, BigDecimal> monthTotals;
    private BigDecimal grandTotal;
    private Long totalRows;
}
