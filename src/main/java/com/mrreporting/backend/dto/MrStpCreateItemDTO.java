package com.mrreporting.backend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MrStpCreateItemDTO {
    private Long fromAreaId;
    private Long toAreaId;
    private String areaType;
    private BigDecimal distance;
    private Integer frequencyVisit;
}
