package com.mrreporting.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProviderTransferDTO {
    private List<Long> providerIds;
    private Long newEmployeeId;
    private Long newAreaId;
}