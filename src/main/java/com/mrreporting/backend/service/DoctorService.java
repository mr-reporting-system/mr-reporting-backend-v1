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
        Doctor doctor = new Doctor();
        mapBasicDetails(doctor, dto);

        // New doctors are pending approval
        doctor.setIsActive(false);
        doctor.setRequestStatus("ADDITION");

        setDoctorRelationships(doctor, dto);
        handleNestedData(doctor, dto);

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
        doctor.setAadhaarNo(dto.getAadhaarNo());
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
        if (dto.getChildren() != null) {
            for (DoctorChildDTO childDto : dto.getChildren()) {
                DoctorChild child = new DoctorChild();
                child.setChildName(childDto.getChildName());
                child.setChildAge(childDto.getChildAge());
                doctor.addChild(child);
            }
        }

        if (dto.getLocations() != null) {
            for (DoctorVisitLocationDTO locDto : dto.getLocations()) {
                DoctorVisitLocation location = new DoctorVisitLocation();
                location.setCity(locDto.getCity());
                location.setSessionType(locDto.getSessionType());

                if (locDto.getSlots() != null) {
                    for (DoctorVisitSlotDTO slotDto : locDto.getSlots()) {
                        DoctorVisitSlot slot = new DoctorVisitSlot();
                        slot.setFromTime(slotDto.getFromTime());
                        slot.setToTime(slotDto.getToTime());
                        location.addSlot(slot);
                    }
                }
                doctor.addLocation(location);
            }
        }
    }

    // Only returns "Approved" doctors for general use
    public List<Doctor> getAllActiveDoctors() {
        return doctorRepository.findByIsActiveTrue();
    }

    // Only returns "Approved" doctors for a specific area
    public List<Doctor> getDoctorsByArea(Long areaId) {
        return doctorRepository.findByAreaIdAndIsActiveTrue(areaId);
    }

    // Instead of deleting, flag it for the Approval Master
    @Transactional
    public void requestDoctorDeletion(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + id));

        // Stays active in the field but moves to the "Deletion Requests" table for admin
        doctor.setRequestStatus("DELETION");
        doctorRepository.save(doctor);
    }

    @Transactional
    public void transferDoctors(ProviderTransferDTO dto) {
        Employee newEmployee = employeeRepository.findById(dto.getNewEmployeeId())
                .orElseThrow(() -> new RuntimeException("New Employee not found"));

        Area newArea = areaRepository.findById(dto.getNewAreaId())
                .orElseThrow(() -> new RuntimeException("New Area not found"));

        List<Doctor> doctorsToTransfer = doctorRepository.findAllById(dto.getProviderIds());

        for (Doctor doctor : doctorsToTransfer) {
            doctor.setEmployee(newEmployee);
            doctor.setArea(newArea);
        }

        doctorRepository.saveAll(doctorsToTransfer);
    }
}