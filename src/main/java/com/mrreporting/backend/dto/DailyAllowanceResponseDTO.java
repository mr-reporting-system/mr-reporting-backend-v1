package com.mrreporting.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

// flattened response DTO for the bottom table.
// specialAllowance is calculated server-side as postageStationary + postageFreight
// so the frontend just renders it directly without any math.
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyAllowanceResponseDTO {

    private Long id;
    private Integer stateId;
    private String stateName;
    private Long designationId;
    private String designationName;
    private BigDecimal hqDa;
    private BigDecimal exDa;
    private BigDecimal outDa;
    private BigDecimal mobileAllowance;
    private BigDecimal netAllowance;
    private BigDecimal specialAllowance;   // postageStationary + postageFreight
}