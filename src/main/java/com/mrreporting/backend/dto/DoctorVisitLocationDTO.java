package com.mrreporting.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class DoctorVisitLocationDTO {
    private String city;
    private String sessionType;
    private List<DoctorVisitSlotDTO> slots; // Nested list of slots!
}