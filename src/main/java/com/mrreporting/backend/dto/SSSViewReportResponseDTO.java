package com.mrreporting.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SSSViewReportResponseDTO {
    private List<SSSViewReportRowDTO> rows;
    private SSSViewReportRowDTO totals;
    private Long totalRows;
}
