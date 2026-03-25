package com.mrreporting.backend.dto;

import lombok.Data;
import java.util.List;

// top-level request body sent when admin clicks "Create FRC".
// designationIds: one or more designations selected at the top.
// rows: one or more rule rows added via the + button.
// backend will create designationIds.size() × rows.size() records.
@Data
public class FareRateCardCreateDTO {

    private List<Long> designationIds;
    private List<FareRateCardRowDTO> rows;
}