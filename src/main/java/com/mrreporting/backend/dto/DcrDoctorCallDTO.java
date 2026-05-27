package com.mrreporting.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class DcrDoctorCallDTO {
    private Long doctorId;
    private Long jointWithManagerId;
    private String remarks;
    private List<Long> productListIds;
}
