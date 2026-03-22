package com.mrreporting.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class MapHierarchyDTO {
    private List<Long> employeeIds;
    private Long managerId;
}