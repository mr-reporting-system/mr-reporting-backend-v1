package com.mrreporting.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrmDoctorResponseDTO {

    private Long id;
    private String doctorName;
    private String doctorCode;
    private String specialization;
    private String category;
    private String phone;
    private String crmStatus;
    private String sponsorshipStatus;
}