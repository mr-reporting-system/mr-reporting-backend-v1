package com.mrreporting.backend.service;

import com.mrreporting.backend.dto.ApprovalSummaryDTO;
import com.mrreporting.backend.entity.Area;
import com.mrreporting.backend.entity.Doctor;
import com.mrreporting.backend.entity.Provider;
import com.mrreporting.backend.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ApprovalService {

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private ProviderRepository providerRepository;

    public Map<String, Object> getDashboardCounts() {
        Map<String, Object> counts = new HashMap<>();

        // Addition Requests (is_active = false, status = 'ADDITION')
        Map<String, Long> additions = new HashMap<>();
        additions.put("area", areaRepository.countByIsActiveFalseAndRequestStatus("ADDITION"));
        additions.put("doctor", doctorRepository.countByIsActiveFalseAndRequestStatus("ADDITION"));
        additions.put("provider", providerRepository.countByIsActiveFalseAndRequestStatus("ADDITION"));

        // Deletion Requests (is_active = false, status = 'DELETION')
        // NOTE: We use is_active=false because AreaService.requestAreaDeletion now sets it to false
        Map<String, Long> deletions = new HashMap<>();
        deletions.put("area", areaRepository.countByIsActiveFalseAndRequestStatus("DELETION"));
        doctorRepository.countByIsActiveFalseAndRequestStatus("DELETION");
        deletions.put("doctor", doctorRepository.countByIsActiveFalseAndRequestStatus("DELETION"));
        deletions.put("provider", providerRepository.countByIsActiveFalseAndRequestStatus("DELETION"));

        counts.put("additions", additions);
        counts.put("deletions", deletions);

        return counts;
    }

    public List<ApprovalSummaryDTO> getCategorySummary(String category, String type) {
        // Now both Addition and Deletion are is_active = false while pending
        Boolean isActive = false;
        String status = type.toUpperCase();

        return switch (category.toLowerCase()) {
            case "area" -> areaRepository.getAreaSummary(isActive, status);
            case "doctor" -> doctorRepository.getDoctorSummary(isActive, status);
            case "provider" -> providerRepository.getProviderSummary(isActive, status);
            default -> throw new IllegalArgumentException("Invalid category: " + category);
        };
    }

    public List<?> getPendingDetails(String category, String type, Long employeeId) {
        Boolean isActive = false;
        String status = type.toUpperCase();

        return switch (category.toLowerCase()) {
            case "area" -> areaRepository.findByEmployeeIdAndIsActiveAndRequestStatus(employeeId, isActive, status);
            case "doctor" -> doctorRepository.findByEmployeeIdAndIsActiveAndRequestStatus(employeeId, isActive, status);
            case "provider" -> providerRepository.findByEmployeeIdAndIsActiveAndRequestStatus(employeeId, isActive, status);
            default -> throw new IllegalArgumentException("Invalid category");
        };
    }

    @Transactional
    public void approveRecords(String category, String type, List<Long> ids) {
        boolean isDeletion = type.equalsIgnoreCase("DELETION");

        switch (category.toLowerCase()) {
            case "area" -> {
                List<Area> areas = areaRepository.findAllById(ids);
                areas.forEach(a -> {
                    if (isDeletion) {
                        // Soft Delete: Hide from UI and mark as permanently deleted
                        a.setIsActive(false);
                        a.setRequestStatus("DELETED");
                    } else {
                        // Approve Addition: Make visible and clear request status
                        a.setIsActive(true);
                        a.setRequestStatus(null);
                    }
                });
                areaRepository.saveAll(areas);
            }
            case "doctor" -> {
                List<Doctor> doctors = doctorRepository.findAllById(ids);
                doctors.forEach(d -> {
                    if (isDeletion) {
                        d.setIsActive(false);
                        d.setRequestStatus("DELETED");
                    } else {
                        d.setIsActive(true);
                        d.setRequestStatus(null);
                    }
                });
                doctorRepository.saveAll(doctors);
            }
            case "provider" -> {
                List<Provider> providers = providerRepository.findAllById(ids);
                providers.forEach(p -> {
                    if (isDeletion) {
                        p.setIsActive(false);
                        p.setRequestStatus("DELETED");
                    } else {
                        p.setIsActive(true);
                        p.setRequestStatus(null);
                    }
                });
                providerRepository.saveAll(providers);
            }
        }
    }

    @Transactional
    public void rejectRecords(String category, String type, List<Long> ids) {
        boolean isAddition = type.equalsIgnoreCase("ADDITION");

        switch (category.toLowerCase()) {
            case "area" -> {
                if (isAddition) {
                    areaRepository.deleteAllById(ids); // If new request rejected, remove it
                } else {
                    List<Area> areas = areaRepository.findAllById(ids);
                    areas.forEach(a -> {
                        a.setIsActive(true); // Bring back to active list
                        a.setRequestStatus(null); // Clear the deletion flag
                    });
                    areaRepository.saveAll(areas);
                }
            }
            case "doctor" -> {
                if (isAddition) {
                    doctorRepository.deleteAllById(ids);
                } else {
                    List<Doctor> doctors = doctorRepository.findAllById(ids);
                    doctors.forEach(d -> {
                        d.setIsActive(true);
                        d.setRequestStatus(null);
                    });
                    doctorRepository.saveAll(doctors);
                }
            }
            case "provider" -> {
                if (isAddition) {
                    providerRepository.deleteAllById(ids);
                } else {
                    List<Provider> providers = providerRepository.findAllById(ids);
                    providers.forEach(p -> {
                        p.setIsActive(true);
                        p.setRequestStatus(null);
                    });
                    providerRepository.saveAll(providers);
                }
            }
        }
    }
}