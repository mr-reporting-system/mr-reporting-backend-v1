package com.mrreporting.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class DeleteStockistMappingsRequestDTO {
    private List<Long> mappingIds;
}
