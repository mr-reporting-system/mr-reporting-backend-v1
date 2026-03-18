package com.mrreporting.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "employees")
@Data
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "designation_id") // This tells Spring Boot which SQL column to look at
    private Designation designation;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "state_id")
    private State state;

    @ManyToOne
    @JoinColumn(name = "district_id")
    private District district;

    @ManyToOne
    @JoinColumn(name = "reporting_manager_id")
    private Employee reportingManager;

    // --- Personal Information ---
    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 15)
    private String mobile;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    private LocalDate dob;

    @Column(length = 10)
    private String gender;

    @Column(length = 50)
    private String religion;

    @Column(unique = true, length = 12)
    private String aadhar;

    @Column(unique = true, length = 10)
    private String pan;

    // --- Address & Banking ---
    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "bank_name", length = 100)
    private String bankName;

    @Column(name = "bank_account_number", unique = true, length = 50)
    private String bankAccountNumber;

    @Column(name = "ifsc_code", length = 20)
    private String ifscCode;

    // --- Company Information ---
    @Column(name = "date_of_joining")
    private LocalDate dateOfJoining;

    @Column(name = "date_of_reporting")
    private LocalDate dateOfReporting;

    @Column(name = "date_of_confirmation")
    private LocalDate dateOfConfirmation;

    @Column(name = "user_code", unique = true, length = 50)
    private String userCode;

    // Getters and Setters omitted for brevity
}