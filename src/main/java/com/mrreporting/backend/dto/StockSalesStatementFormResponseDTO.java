package com.mrreporting.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockSalesStatementFormResponseDTO {

    private Long statementId;

    private Integer stateId;
    private String stateName;

    private Integer districtId;
    private String districtName;

    private Long employeeId;
    private String employeeName;

    private Long stockistId;
    private String stockistName;

    private Integer month;
    private Integer year;

    private Boolean existing;

    private List<StockSalesStatementRowResponseDTO> rows;
}
