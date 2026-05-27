package com.mrreporting.backend.service;

import com.mrreporting.backend.dto.AreaDTO;
import com.mrreporting.backend.dto.AreaTransferDTO;
import com.mrreporting.backend.entity.*;
import com.mrreporting.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AreaService {

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Transactional
    public Area saveArea(AreaDTO areaDTO) {
        validateUniqueAreaCode(areaDTO.getAreaCode(), null);

        Area area = new Area();
        area.setAreaName(areaDTO.getAreaName());
        area.setAreaCode(areaDTO.getAreaCode());
        area.setAreaType(areaDTO.getAreaType());

        // Logic Update: New areas are pending approval
        area.setIsActive(false);
        area.setRequestStatus("ADDITION");

        State state = stateRepository.findById(areaDTO.getStateId())
                .orElseThrow(() -> new RuntimeException("State not found"));
        area.setState(state);

        District district = districtRepository.findById(areaDTO.getDistrictId())
                .orElseThrow(() -> new RuntimeException("District not found"));
        area.setDistrict(district);

        Employee employee = employeeRepository.findById(areaDTO.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        area.setEmployee(employee);

        return areaRepository.save(area);
    }

    // Only returns "Approved" areas for general use
    public List<Area> getAllActiveAreas() {
        return areaRepository.findByIsActiveTrue();
    }

    @Transactional
    public Area updateArea(Long id, AreaDTO dto) {
        validateUniqueAreaCode(dto.getAreaCode(), id);

        Area existingArea = areaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Area not found with id: " + id));

        existingArea.setAreaName(dto.getAreaName());
        existingArea.setAreaCode(dto.getAreaCode());
        existingArea.setAreaType(dto.getAreaType());

        existingArea.setState(stateRepository.findById(dto.getStateId())
                .orElseThrow(() -> new RuntimeException("State not found")));

        existingArea.setDistrict(districtRepository.findById(dto.getDistrictId())
                .orElseThrow(() -> new RuntimeException("District not found")));

        existingArea.setEmployee(employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found")));

        return areaRepository.save(existingArea);
    }

    // Instead of deleting, we flag it for the Approval Master
    @Transactional
    public void requestAreaDeletion(Long id) {
        Area area = areaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Area not found with id: " + id));

        // Match doctor/provider approval behavior: keep it active but flag it for deletion approval.
        area.setRequestStatus("DELETION");
        areaRepository.save(area);
    }

    // In an approval system, "Hard Delete" only happens when a Reject button is pressed.
    @Transactional
    public void hardDeleteArea(Long id) {
        areaRepository.deleteById(id);
    }

    public List<Area> getFilteredAreas(Integer stateId, Integer districtId, Long employeeId) {
        return areaRepository.findFilteredAreas(stateId, districtId, employeeId);
    }

    @Transactional
    public void transferAreas(AreaTransferDTO dto) {
        Employee newEmployee = employeeRepository.findById(dto.getNewEmployeeId())
                .orElseThrow(() -> new RuntimeException("New Employee not found"));

        List<Area> areasToTransfer = areaRepository.findAllById(dto.getAreaIds());

        for (Area area : areasToTransfer) {
            area.setEmployee(newEmployee);
        }

        areaRepository.saveAll(areasToTransfer);
    }

    public List<Area> getAreasForEmployee(Long employeeId) {
        return areaRepository.findAreasByEmployeeId(employeeId);
    }

    private void validateUniqueAreaCode(String areaCode, Long existingAreaId) {
        if (areaCode == null || areaCode.isBlank()) {
            return;
        }

        boolean duplicateExists = existingAreaId == null
                ? areaRepository.existsByAreaCodeIgnoreCase(areaCode)
                : areaRepository.existsByAreaCodeIgnoreCaseAndIdNot(areaCode, existingAreaId);

        if (duplicateExists) {
            throw new RuntimeException("Area code already exists. Use a different area code, or call the update API for an existing area.");
        }
    }
}
