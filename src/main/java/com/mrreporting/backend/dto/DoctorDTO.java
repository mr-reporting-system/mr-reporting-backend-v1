package com.mrreporting.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class DoctorDTO {
    // --- Core Relationships (IDs only) 🗺️ ---
    private Integer stateId;
    private Integer districtId;
    private Long employeeId;
    private Long areaId;

    // --- Basic Doctor Info ✍️ ---
    private String doctorCode;
    private String doctorName;
    private String mslNo;
    private String category;
    private String degree;
    private String specialization;
    private String phone;
    private String gender;
    private String address;
    private String licenceNo;
    private String email;
    private Integer frequencyVisit;

    // --- Nested Lists (Additional Info) 👨‍👩‍👧‍👦 ---
    private List<DoctorChildDTO> children;
    private List<DoctorVisitLocationDTO> locations;
}