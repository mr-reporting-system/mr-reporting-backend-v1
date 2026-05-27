package com.mrreporting.backend.service;

import com.mrreporting.backend.dto.DropdownOptionDTO;
import com.mrreporting.backend.dto.MrStpCreateItemDTO;
import com.mrreporting.backend.dto.MrStpCreateRequestDTO;
import com.mrreporting.backend.dto.MrStpCreateResponseDTO;
import com.mrreporting.backend.dto.MrStpRowDTO;
import com.mrreporting.backend.dto.StpRequestDTO;
import com.mrreporting.backend.entity.Area;
import com.mrreporting.backend.entity.Employee;
import com.mrreporting.backend.entity.Stp;
import com.mrreporting.backend.repository.AreaRepository;
import com.mrreporting.backend.repository.StpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class MrStpCreationService {

    @Autowired
    private MrContextService mrContextService;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private StpRepository stpRepository;

    @Autowired
    private StpService stpService;

    @Transactional(readOnly = true)
    public List<DropdownOptionDTO> getAreasForCurrentMr() {
        Employee employee = mrContextService.getCurrentMr();

        return areaRepository.findVisibleMrAreasByEmployeeId(employee.getId())
                .stream()
                .map(area -> new DropdownOptionDTO(
                        area.getId(),
                        area.getAreaName(),
                        area.getAreaType()
                ))
                .toList();
    }

    @Transactional
    public MrStpCreateResponseDTO createStpsForCurrentMr(MrStpCreateRequestDTO dto) {
        if (dto == null || dto.getRoutes() == null || dto.getRoutes().isEmpty()) {
            throw new RuntimeException("At least one STP route is required.");
        }

        Employee employee = mrContextService.getCurrentMr();
        List<Stp> created = new ArrayList<>();

        for (MrStpCreateItemDTO route : dto.getRoutes()) {
            validateRoute(route);

            Area fromArea = mrContextService.getOwnedActiveArea(route.getFromAreaId(), employee);
            Area toArea = mrContextService.getOwnedActiveArea(route.getToAreaId(), employee);

            StpRequestDTO request = new StpRequestDTO();
            request.setDesignationId(employee.getDesignation() != null ? employee.getDesignation().getId() : null);
            request.setEmployeeId(employee.getId());
            request.setFromAreaId(fromArea.getId());
            request.setToAreaId(toArea.getId());
            request.setAreaType(clean(route.getAreaType()));
            request.setDistance(route.getDistance());
            request.setFrequencyVisit(route.getFrequencyVisit());
            request.setFrc(null);

            created.add(stpService.createStp(request));
        }

        return new MrStpCreateResponseDTO(employee.getId(), (long) created.size());
    }

    @Transactional(readOnly = true)
    public List<MrStpRowDTO> getStpsForCurrentMr() {
        Employee employee = mrContextService.getCurrentMr();

        return stpRepository.findByEmployeeIdOrderByCreatedAtDescIdDesc(employee.getId())
                .stream()
                .map(this::toRow)
                .toList();
    }

    private MrStpRowDTO toRow(Stp stp) {
        boolean adminApproved = Boolean.TRUE.equals(stp.getAdminApproved())
                || (Boolean.TRUE.equals(stp.getIsActive()) && "APPROVED".equalsIgnoreCase(stp.getRequestStatus()));

        return new MrStpRowDTO(
                stp.getId(),
                stp.getFromArea() != null ? stp.getFromArea().getAreaName() : null,
                stp.getToArea() != null ? stp.getToArea().getAreaName() : null,
                stp.getAreaType(),
                stp.getDistance(),
                stp.getFrequencyVisit(),
                Boolean.TRUE.equals(stp.getManagerApproved()),
                adminApproved,
                stp.getRequestStatus()
        );
    }

    private void validateRoute(MrStpCreateItemDTO route) {
        if (route == null) {
            throw new RuntimeException("Route payload is invalid.");
        }
        if (route.getFromAreaId() == null) {
            throw new RuntimeException("fromAreaId is required.");
        }
        if (route.getToAreaId() == null) {
            throw new RuntimeException("toAreaId is required.");
        }
        if (route.getDistance() == null) {
            throw new RuntimeException("distance is required.");
        }
        if (route.getDistance().signum() < 0) {
            throw new RuntimeException("distance cannot be negative.");
        }
        if (route.getFrequencyVisit() == null) {
            throw new RuntimeException("frequencyVisit is required.");
        }
        if (route.getFrequencyVisit() < 0) {
            throw new RuntimeException("frequencyVisit cannot be negative.");
        }
        if (route.getAreaType() == null || route.getAreaType().isBlank()) {
            throw new RuntimeException("areaType is required.");
        }
    }

    private String clean(String value) {
        return value.trim();
    }
}
