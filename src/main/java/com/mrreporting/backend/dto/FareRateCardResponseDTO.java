package com.mrreporting.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

// flattened DTO used to populate the bottom table.
// avoids sending the full nested designation object to the frontend.
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FareRateCardResponseDTO {

    private Long id;
    private Long designationId;
    private String designationName;  // "MR", "ASM", etc. — shown in Designation column
    private String name;             // same as designationName, shown in Name column
    private BigDecimal fromDistance;
    private BigDecimal toDistance;
    private String allowanceType;
    private String applicableTo;
    private BigDecimal fare;
    private String frcCode;
    private String description;
}