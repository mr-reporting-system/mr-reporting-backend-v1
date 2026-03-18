package com.mrreporting.backend.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ProviderDTO {
    // --- Relationships (IDs only) 🗺️ ---
    private Integer stateId;
    private Integer districtId;
    private Long employeeId;
    private Long areaId;

    // --- Core Provider Details ✍️ ---
    private String type; // 'Chemist' or 'Stockist'
    private String providerCode;
    private String providerName;
    private String phone;
    private String aadhaarNo;

    // --- Additional Info (Other Info Section) 📋 ---
    private String ownerName;
    private LocalDate ownerDob;
    private LocalDate ownerDoa;
    private LocalDate shopDoa;
    private String address;
    private String city;
    private String email;
    private String panCard;
    private String gstNumber;
    private String licenceNo;
    private String category;
}