package com.mrreporting.backend.service;

import com.mrreporting.backend.dto.StpRequestDTO;
import com.mrreporting.backend.entity.*;
import com.mrreporting.backend.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StpService {

    @Autowired private StpRepository stpRepository;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private DesignationRepository designationRepository;
    @Autowired private AreaRepository areaRepository; // Assuming this is your Area repository

    public Stp createStp(StpRequestDTO dto) {
        Stp stp = new Stp();

        // Look up and attach the relational data
        stp.setDesignation(designationRepository.findById(dto.getDesignationId())
                .orElseThrow(() -> new RuntimeException("Designation not found")));
        stp.setEmployee(employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found")));
        stp.setFromArea(areaRepository.findById(dto.getFromAreaId())
                .orElseThrow(() -> new RuntimeException("From Area not found")));
        stp.setToArea(areaRepository.findById(dto.getToAreaId())
                .orElseThrow(() -> new RuntimeException("To Area not found")));

        // Set the standard fields
        stp.setAreaType(dto.getAreaType());
        stp.setFrc(dto.getFrc());
        stp.setDistance(dto.getDistance());
        stp.setFrequencyVisit(dto.getFrequencyVisit());
        stp.setIsActive(false);
        stp.setRequestStatus("PENDING");

        return stpRepository.save(stp);
    }

    // Only return approved STPs for an employee (used by MR's own view)
    public List<Stp> getStpsByEmployeeId(Long employeeId) {
        return stpRepository.findByEmployeeIdAndIsActiveTrue(employeeId);
    }

    @Transactional
    public void deleteStps(List<Long> stpIds) {
        if (stpIds != null && !stpIds.isEmpty()) {
            stpRepository.deleteAllById(stpIds);
        }
    }

    @Transactional
    public Stp updateStp(Long id, StpRequestDTO dto) {
        Stp existingStp = stpRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("STP not found"));

        existingStp.setDesignation(designationRepository.findById(dto.getDesignationId())
                .orElseThrow(() -> new RuntimeException("Designation not found")));
        existingStp.setEmployee(employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found")));
        existingStp.setFromArea(areaRepository.findById(dto.getFromAreaId())
                .orElseThrow(() -> new RuntimeException("From Area not found")));
        existingStp.setToArea(areaRepository.findById(dto.getToAreaId())
                .orElseThrow(() -> new RuntimeException("To Area not found")));

        existingStp.setAreaType(dto.getAreaType());
        existingStp.setFrc(dto.getFrc());
        existingStp.setDistance(dto.getDistance());
        existingStp.setFrequencyVisit(dto.getFrequencyVisit());

        return stpRepository.save(existingStp);
    }
}