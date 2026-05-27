package com.mrreporting.backend.dto;

import lombok.Data;

@Data
public class DcrChemistStockistCallDTO {
    private Long chemistStockistId;
    private String type;
    private Long jointWithManagerId;
    private String remarks;
}
