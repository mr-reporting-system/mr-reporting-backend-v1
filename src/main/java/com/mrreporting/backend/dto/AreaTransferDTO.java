package com.mrreporting.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class AreaTransferDTO {
    private List<Long> areaIds;
    private Long newEmployeeId;
}