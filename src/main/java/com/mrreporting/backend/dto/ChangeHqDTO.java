package com.mrreporting.backend.dto;

import lombok.Data;

@Data
public class ChangeHqDTO {
    private Long employeeId;
    private Integer stateId;
    private Integer districtId;
}