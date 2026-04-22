package com.mrreporting.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockistMappingReportResponseDTO {
    private List<StockistMappingReportRowDTO> rows;
    private Long totalRows;
}
