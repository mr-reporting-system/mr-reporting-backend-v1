package com.mrreporting.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteStockistMappingsResponseDTO {
    private Long deletedCount;
    private List<Long> mappingIds;
}
