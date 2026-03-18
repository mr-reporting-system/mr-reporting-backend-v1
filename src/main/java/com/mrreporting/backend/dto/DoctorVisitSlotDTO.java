package com.mrreporting.backend.dto;

import lombok.Data;
import java.time.LocalTime;

@Data
public class DoctorVisitSlotDTO {
    private LocalTime fromTime;
    private LocalTime toTime;
}