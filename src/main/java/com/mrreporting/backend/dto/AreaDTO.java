package com.mrreporting.backend.dto;

import lombok.Data;

@Data
public class AreaDTO {
    private String areaName;
    private String areaCode;
    private String areaType;

    // We only need the IDs to link the relationships
    private Integer stateId;
    private Integer districtId;
    private Long employeeId;
}