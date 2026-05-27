package com.mrreporting.backend.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class DcrSubmitRequestDTO {
    private LocalDate dcrDate;
    private String workingStatus;
    private Long jointWorkManagerId;
    private String remarks;
    private Boolean isDeviate;
    private String deviateReason;
    private List<DcrAreaPairDTO> travelAreas;
    private List<DcrDoctorCallDTO> doctorCalls;
    private List<DcrChemistStockistCallDTO> chemistStockistCalls;
    private List<DcrNextMeetingDTO> nextMeetings;
    private List<DcrExpenseItemDTO> expenses;
}
