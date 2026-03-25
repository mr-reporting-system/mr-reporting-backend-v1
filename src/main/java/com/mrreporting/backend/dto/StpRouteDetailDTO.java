package com.mrreporting.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StpRouteDetailDTO {

    private Long id;                  // STP record ID — used for approve/delete actions
    private String fromAreaName;      // Human-readable area name
    private String toAreaName;        // Human-readable area name
    private BigDecimal distance;
    private String areaType;
    private Integer frc;
    private Integer frequencyVisit;
    private String requestStatus;     // "PENDING" | "APPROVED" | "DELETED"
}