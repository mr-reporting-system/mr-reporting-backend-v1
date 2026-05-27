package com.mrreporting.backend.service;

import com.mrreporting.backend.dto.DoctorDTO;
import com.mrreporting.backend.dto.DropdownOptionDTO;
import com.mrreporting.backend.dto.ProviderDTO;
import com.mrreporting.backend.entity.Area;
import com.mrreporting.backend.entity.Doctor;
import com.mrreporting.backend.entity.Employee;
import com.mrreporting.backend.entity.Provider;
import com.mrreporting.backend.repository.AreaRepository;
import com.mrreporting.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MrProviderCreationService {

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private ProviderService providerService;

    @Autowired
    private MrContextService mrContextService;

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

    @Transactional(readOnly = true)
    public List<DropdownOptionDTO> getProducts() {
        return productRepository.findAllByOrderByProductNameAsc()
                .stream()
                .map(product -> new DropdownOptionDTO(
                        product.getId(),
                        product.getProductName(),
                        product.getProductCode()
                ))
                .toList();
    }

    @Transactional
    public Doctor createDoctorForCurrentMr(DoctorDTO dto) {
        if (dto == null) {
            throw new RuntimeException("Doctor payload is required.");
        }

        Employee employee = mrContextService.getCurrentMr();
        Area area = mrContextService.getOwnedActiveArea(dto.getAreaId(), employee);

        dto.setEmployeeId(employee.getId());
        dto.setStateId(area.getState().getId());
        dto.setDistrictId(area.getDistrict().getId());
        dto.setAreaId(area.getId());

        return doctorService.saveDoctor(dto);
    }

    @Transactional
    public Provider createProviderForCurrentMr(ProviderDTO dto) {
        if (dto == null) {
            throw new RuntimeException("Provider payload is required.");
        }

        Employee employee = mrContextService.getCurrentMr();
        Area area = mrContextService.getOwnedActiveArea(dto.getAreaId(), employee);

        dto.setType(normalizeProviderType(dto.getType()));
        dto.setEmployeeId(employee.getId());
        dto.setStateId(area.getState().getId());
        dto.setDistrictId(area.getDistrict().getId());
        dto.setAreaId(area.getId());

        return providerService.saveProvider(dto);
    }

    private String normalizeProviderType(String type) {
        if (type == null || type.isBlank()) {
            throw new RuntimeException("type is required.");
        }

        String normalized = type.trim().toUpperCase();
        return switch (normalized) {
            case "CHEMIST" -> "Chemist";
            case "STOCKIST" -> "Stockist";
            default -> throw new RuntimeException("type must be CHEMIST or STOCKIST.");
        };
    }
}
