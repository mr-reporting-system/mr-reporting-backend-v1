package com.mrreporting.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class MrStpCreateRequestDTO {
    private List<MrStpCreateItemDTO> routes;
}
