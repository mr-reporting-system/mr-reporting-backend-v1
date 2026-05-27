package com.mrreporting.backend.dto;

import lombok.Data;

@Data
public class DcrNextMeetingDTO {
    private Long meetingWithManagerId;
    private String subject;
    private String remarks;
}
