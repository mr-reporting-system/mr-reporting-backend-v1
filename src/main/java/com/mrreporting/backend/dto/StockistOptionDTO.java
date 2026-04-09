package com.mrreporting.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockistOptionDTO {
    private Long id;
    private String providerName;
    private String type;
}
