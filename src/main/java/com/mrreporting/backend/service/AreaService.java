package com.mrreporting.backend.service;

import com.mrreporting.backend.dto.AreaDTO;
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
        // 1. Create a new Area entity
        Area area = new Area();
        area.setAreaName(areaDTO.getAreaName());
        area.setAreaCode(areaDTO.getAreaCode());
        area.setAreaType(areaDTO.getAreaType());

        // 2. Fetch and set the State 🗺️
        State state = stateRepository.findById(areaDTO.getStateId())
                .orElseThrow(() -> new RuntimeException("State not found"));
        area.setState(state);

        // 3. Fetch and set the District 📍
        District district = districtRepository.findById(areaDTO.getDistrictId())
                .orElseThrow(() -> new RuntimeException("District not found"));
        area.setDistrict(district);

        // 4. Fetch and set the Employee 👔
        Employee employee = employeeRepository.findById(areaDTO.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        area.setEmployee(employee);

        // 5. Save the Area (Defaults like status and counters are handled in the Entity)
        return areaRepository.save(area);
    }

    public List<Area> getAllAreas() {
        return areaRepository.findAll();
    }

    @Transactional
    public Area updateArea(Long id, AreaDTO dto) {
        // 1. Find the existing area in the database
        Area existingArea = areaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Area not found with id: " + id));

        // 2. Map direct fields (Name, Code)
        existingArea.setAreaName(dto.getAreaName());
        existingArea.setAreaCode(dto.getAreaCode());

        // 3. Update Relationships (Look up by new IDs from DTO)
        existingArea.setState(stateRepository.findById(dto.getStateId())
                .orElseThrow(() -> new RuntimeException("State not found")));

        existingArea.setDistrict(districtRepository.findById(dto.getDistrictId())
                .orElseThrow(() -> new RuntimeException("District not found")));

        existingArea.setEmployee(employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found")));

        // 4. Save the updated entity
        return areaRepository.save(existingArea);
    }

    public void deleteArea(Long id) {
        // Simple check to ensure it exists before trying to delete
        if (!areaRepository.existsById(id)) {
            throw new RuntimeException("Area not found with id: " + id);
        }

        // Standard JPA delete call
        areaRepository.deleteById(id);
    }

    public List<Area> getFilteredAreas(Integer stateId, Integer districtId, Long employeeId) {
        return areaRepository.findFilteredAreas(stateId, districtId, employeeId);
    }
}