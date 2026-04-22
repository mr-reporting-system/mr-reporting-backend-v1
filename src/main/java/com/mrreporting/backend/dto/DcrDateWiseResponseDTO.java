package com.mrreporting.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DcrDateWiseResponseDTO {
    private Long employeeId;
    private String employeeName;
    private String managerName;
    private DcrDateWiseSummaryDTO summary;
    private List<DcrDateWiseRowDTO> rows;
    private Long totalRows;
}
