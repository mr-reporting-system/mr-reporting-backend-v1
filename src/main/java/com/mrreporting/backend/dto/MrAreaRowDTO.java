package com.mrreporting.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MrAreaRowDTO {
    private Long areaId;
    private String headquarterName;
    private Long employeeId;
    private String employeeName;
    private String areaName;
    private String areaCode;
    private String areaType;
    private Boolean status;

    @JsonProperty("id")
    public Long getId() {
        return areaId;
    }
}
