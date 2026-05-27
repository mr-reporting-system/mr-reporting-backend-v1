package com.mrreporting.backend.service;

import com.mrreporting.backend.dto.AreaDTO;
import com.mrreporting.backend.dto.MrAreaRequestDTO;
import com.mrreporting.backend.dto.MrAreaRowDTO;
import com.mrreporting.backend.entity.Area;
import com.mrreporting.backend.entity.Employee;
import com.mrreporting.backend.repository.AreaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MrAreaCreationService {

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private AreaService areaService;

    @Autowired
    private MrContextService mrContextService;

    @Transactional(readOnly = true)
    public List<MrAreaRowDTO> getAreasForCurrentMr() {
        Employee employee = mrContextService.getCurrentMr();

        return areaRepository.findVisibleMrAreasByEmployeeId(employee.getId())
                .stream()
                .map(this::toRow)
                .toList();
    }

    @Transactional
    public MrAreaRowDTO createAreaForCurrentMr(MrAreaRequestDTO dto) {
        validateRequest(dto);

        Employee employee = mrContextService.getCurrentMr();

        AreaDTO areaDTO = buildSharedAreaRequest(dto, employee);
        Area savedArea = areaService.saveArea(areaDTO);
        return toRow(savedArea);
    }

    @Transactional
    public MrAreaRowDTO updateAreaForCurrentMr(Long areaId, MrAreaRequestDTO dto) {
        validateRequest(dto);

        Employee employee = mrContextService.getCurrentMr();
        mrContextService.getOwnedActiveArea(areaId, employee);

        AreaDTO areaDTO = buildSharedAreaRequest(dto, employee);
        Area updatedArea = areaService.updateArea(areaId, areaDTO);
        return toRow(updatedArea);
    }

    @Transactional
    public void requestAreaDeletionForCurrentMr(Long areaId) {
        Employee employee = mrContextService.getCurrentMr();
        mrContextService.getOwnedActiveArea(areaId, employee);
        areaService.requestAreaDeletion(areaId);
    }

    private AreaDTO buildSharedAreaRequest(MrAreaRequestDTO dto, Employee employee) {
        AreaDTO areaDTO = new AreaDTO();
        areaDTO.setAreaName(clean(dto.getAreaName()));
        areaDTO.setAreaCode(clean(dto.getAreaCode()));
        areaDTO.setAreaType(clean(dto.getAreaType()));
        areaDTO.setEmployeeId(employee.getId());
        areaDTO.setStateId(employee.getState().getId());
        areaDTO.setDistrictId(employee.getDistrict().getId());
        return areaDTO;
    }

    private void validateRequest(MrAreaRequestDTO dto) {
        if (dto == null) {
            throw new RuntimeException("Area payload is required.");
        }
        if (isBlank(dto.getAreaName())) {
            throw new RuntimeException("areaName is required.");
        }
        if (isBlank(dto.getAreaCode())) {
            throw new RuntimeException("areaCode is required.");
        }
        if (isBlank(dto.getAreaType())) {
            throw new RuntimeException("areaType is required.");
        }
    }

    private MrAreaRowDTO toRow(Area area) {
        return new MrAreaRowDTO(
                area.getId(),
                area.getDistrict() != null ? area.getDistrict().getDistrictName() : null,
                area.getEmployee() != null ? area.getEmployee().getId() : null,
                area.getEmployee() != null ? area.getEmployee().getName() : null,
                area.getAreaName(),
                area.getAreaCode(),
                area.getAreaType(),
                area.getIsActive()
        );
    }

    private String clean(String value) {
        return value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
