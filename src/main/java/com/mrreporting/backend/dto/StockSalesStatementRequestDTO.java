package com.mrreporting.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class StockSalesStatementRequestDTO {

    private Long stockistId;
    private Long providerId;

    private Integer month;
    private Integer year;
    private List<StockSalesStatementRowRequestDTO> rows;

    public Long getResolvedProviderId() {
        return providerId != null ? providerId : stockistId;
    }
}
