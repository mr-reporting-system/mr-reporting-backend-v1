package com.mrreporting.backend.service;

import com.mrreporting.backend.dto.*;
import com.mrreporting.backend.entity.*;
import com.mrreporting.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private StateRepository stateRepository;
    @Autowired
    private DistrictRepository districtRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private AreaRepository areaRepository;

    @Transactional
    public Doctor saveDoctor(DoctorDTO dto) {
        // 1. Create the main Doctor entity and map basic fields
        Doctor doctor = new Doctor();
        mapBasicDetails(doctor, dto);

        // 2. Look up and set the relational objects (State, District, etc.) 🗺️
        setDoctorRelationships(doctor, dto);

        // 3. Handle the "One-to-Many" lists (Children and Locations) 👨‍👩‍👧‍👦
        handleNestedData(doctor, dto);

        // 4. Save everything at once! 💾
        // (CascadeType.ALL in the Entity handles the children/locations)
        return doctorRepository.save(doctor);
    }

    private void mapBasicDetails(Doctor doctor, DoctorDTO dto) {
        doctor.setDoctorName(dto.getDoctorName());
        doctor.setDoctorCode(dto.getDoctorCode());
        doctor.setMslNo(dto.getMslNo());
        doctor.setCategory(dto.getCategory());
        doctor.setDegree(dto.getDegree());
        doctor.setSpecialization(dto.getSpecialization());
        doctor.setPhone(dto.getPhone());
        doctor.setGender(dto.getGender());
        doctor.setAddress(dto.getAddress());
        doctor.setLicenceNo(dto.getLicenceNo());
        doctor.setEmail(dto.getEmail());
        doctor.setFrequencyVisit(dto.getFrequencyVisit());
    }

    private void setDoctorRelationships(Doctor doctor, DoctorDTO dto) {
        doctor.setState(stateRepository.findById(dto.getStateId())
                .orElseThrow(() -> new RuntimeException("State not found")));

        doctor.setDistrict(districtRepository.findById(dto.getDistrictId())
                .orElseThrow(() -> new RuntimeException("District not found")));

        doctor.setEmployee(employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found")));

        doctor.setArea(areaRepository.findById(dto.getAreaId())
                .orElseThrow(() -> new RuntimeException("Area not found")));
    }

    private void handleNestedData(Doctor doctor, DoctorDTO dto) {
        // 1. Process Children 🧒
        if (dto.getChildren() != null) {
            for (DoctorChildDTO childDto : dto.getChildren()) {
                DoctorChild child = new DoctorChild();
                child.setChildName(childDto.getChildName());
                child.setChildAge(childDto.getChildAge());

                // This helper method sets the bidirectional link!
                doctor.addChild(child);
            }
        }

        // 2. Process Visit Locations & Time Slots 📍🕒
        if (dto.getLocations() != null) {
            for (DoctorVisitLocationDTO locDto : dto.getLocations()) {
                DoctorVisitLocation location = new DoctorVisitLocation();
                location.setCity(locDto.getCity());
                location.setSessionType(locDto.getSessionType());

                // Process the nested slots inside this specific location
                if (locDto.getSlots() != null) {
                    for (DoctorVisitSlotDTO slotDto : locDto.getSlots()) {
                        DoctorVisitSlot slot = new DoctorVisitSlot();
                        slot.setFromTime(slotDto.getFromTime());
                        slot.setToTime(slotDto.getToTime());

                        // Linking the slot to the location
                        location.addSlot(slot);
                    }
                }

                // Linking the location to the doctor
                doctor.addLocation(location);
            }
        }
    }
}