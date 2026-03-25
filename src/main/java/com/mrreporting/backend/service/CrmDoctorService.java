package com.mrreporting.backend.service;

import com.mrreporting.backend.dto.CrmDoctorResponseDTO;
import com.mrreporting.backend.entity.Doctor;
import com.mrreporting.backend.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CrmDoctorService {

    // CRM Status constants — single source of truth to avoid typos
    private static final String CRM_STATUS_LINKED     = "Linked";
    private static final String CRM_STATUS_NOT_LINKED = "Not Linked";

    @Autowired
    private DoctorRepository doctorRepository;

    // Get doctors by employee + CRM status
    // Used by both tabs in the UI

    public List<CrmDoctorResponseDTO> getDoctorsByCrmStatus(Long employeeId, String crmStatus) {
        validateCrmStatus(crmStatus);

        List<Doctor> doctors = doctorRepository
                .findByEmployeeIdAndIsActiveTrueAndCrmStatus(employeeId, crmStatus);

        return doctors.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // Get the "3 CRM Linked Doctors" badge number

    public long getLinkedDoctorCount(Long employeeId) {
        return doctorRepository
                .countByEmployeeIdAndIsActiveTrueAndCrmStatus(employeeId, CRM_STATUS_LINKED);
    }

    //  LINK: Mark selected doctors as Linked

    @Transactional
    public int linkDoctors(Long employeeId, List<Long> doctorIds) {
        if (doctorIds == null || doctorIds.isEmpty()) {
            throw new IllegalArgumentException("No doctor IDs provided for linking.");
        }

        List<Doctor> doctors = doctorRepository.findAllById(doctorIds);

        // Guard: ensure all fetched doctors are active and belong to the correct employee
        List<Doctor> validDoctors = doctors.stream()
                .filter(d -> Boolean.TRUE.equals(d.getIsActive()))
                .filter(d -> d.getEmployee() != null && d.getEmployee().getId().equals(employeeId))
                .toList();

        if (validDoctors.isEmpty()) {
            throw new RuntimeException(
                    "No valid active doctors found for the given IDs under employee ID: " + employeeId
            );
        }

        validDoctors.forEach(d -> d.setCrmStatus(CRM_STATUS_LINKED));
        doctorRepository.saveAll(validDoctors);

        return validDoctors.size(); // Return actual count updated for response message
    }

    //  UNLINK: Revert selected doctors back to "Not Linked"

    @Transactional
    public int unlinkDoctors(Long employeeId, List<Long> doctorIds) {
        if (doctorIds == null || doctorIds.isEmpty()) {
            throw new IllegalArgumentException("No doctor IDs provided for unlinking.");
        }

        List<Doctor> doctors = doctorRepository.findAllById(doctorIds);

        List<Doctor> validDoctors = doctors.stream()
                .filter(d -> Boolean.TRUE.equals(d.getIsActive()))
                .filter(d -> d.getEmployee() != null && d.getEmployee().getId().equals(employeeId))
                .toList();

        if (validDoctors.isEmpty()) {
            throw new RuntimeException(
                    "No valid active doctors found for the given IDs under employee ID: " + employeeId
            );
        }

        validDoctors.forEach(d -> d.setCrmStatus(CRM_STATUS_NOT_LINKED));
        doctorRepository.saveAll(validDoctors);

        return validDoctors.size();
    }

    //  PRIVATE HELPERS

    private CrmDoctorResponseDTO toResponseDTO(Doctor doctor) {
        return new CrmDoctorResponseDTO(
                doctor.getId(),
                doctor.getDoctorName(),
                doctor.getDoctorCode(),
                doctor.getSpecialization(),
                doctor.getCategory(),
                doctor.getPhone(),
                doctor.getCrmStatus(),
                doctor.getSponsorshipStatus()
        );
    }

    private void validateCrmStatus(String crmStatus) {
        if (!CRM_STATUS_LINKED.equals(crmStatus) && !CRM_STATUS_NOT_LINKED.equals(crmStatus)) {
            throw new IllegalArgumentException(
                    "Invalid crmStatus value: '" + crmStatus + "'. Must be 'Linked' or 'Not Linked'."
            );
        }
    }
}