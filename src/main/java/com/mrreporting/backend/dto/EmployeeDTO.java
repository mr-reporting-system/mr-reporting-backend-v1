package com.mrreporting.backend.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class EmployeeDTO {

    // --- Personal Information ---
    private String name;
    private String mobile;
    private String email;
    private LocalDate dob;
    private String gender;
    private String religion;
    private String aadhar;
    private String pan;

    // --- Address & Banking ---
    private String address;
    private Integer stateId;     // Replaces the State object 🗺️
    private Integer districtId;  // Replaces the District object 🗺️
    private String bankName;
    private String bankAccountNumber;
    private String ifscCode;

    // --- Company & Login Information ---
    private Integer designationId; // Replaces the Designation object 👔
    private Long reportingManagerId; // Replaces the Employee manager object 👔
    private LocalDate dateOfJoining;
    private LocalDate dateOfReporting;
    private LocalDate dateOfConfirmation;
    private String userCode;

    private String password; // Captures the password for the users table 🔒
}