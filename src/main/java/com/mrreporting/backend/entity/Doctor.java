package com.mrreporting.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String registrationNumber;
    private String firstName;
    private String lastName;
    private String specialization;
    private String phoneNumber;
    private String clinicOrHospitalName;
    private String city;
    private String category;

}