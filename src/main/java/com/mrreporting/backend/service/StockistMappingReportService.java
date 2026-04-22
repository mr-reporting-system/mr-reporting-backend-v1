package com.mrreporting.backend.service;

import com.mrreporting.backend.dto.DeleteStockistMappingsRequestDTO;
import com.mrreporting.backend.dto.DeleteStockistMappingsResponseDTO;
import com.mrreporting.backend.dto.StockistMappingReportFilterDTO;
import com.mrreporting.backend.dto.StockistMappingReportResponseDTO;
import com.mrreporting.backend.dto.StockistMappingReportRowDTO;
import com.mrreporting.backend.entity.EmployeeStockistMapping;
import com.mrreporting.backend.repository.EmployeeStockistMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Service
public class StockistMappingReportService {

    @Autowired
    private EmployeeStockistMappingRepository employeeStockistMappingRepository;

    @Transactional(readOnly = true)
    public StockistMappingReportResponseDTO getMappedReport(StockistMappingReportFilterDTO dto) {
        validateViewRequest(dto);

        List<StockistMappingReportRowDTO> rows =
                employeeStockistMappingRepository.findReportRows(
                        isEmpty(dto.getStateIds()),
                        intIdsOrDummy(dto.getStateIds()),
                        isEmpty(dto.getDistrictIds()),
                        intIdsOrDummy(dto.getDistrictIds()),
                        isEmpty(dto.getEmployeeIds()),
                        longIdsOrDummy(dto.getEmployeeIds())
                );

        return new StockistMappingReportResponseDTO(
                rows,
                (long) rows.size()
        );
    }

    @Transactional
    public DeleteStockistMappingsResponseDTO deleteMappings(DeleteStockistMappingsRequestDTO dto) {
        if (dto == null || dto.getMappingIds() == null || dto.getMappingIds().isEmpty()) {
            throw new RuntimeException("At least one mappingId is required.");
        }

        List<Long> mappingIds = new ArrayList<>(new LinkedHashSet<>(dto.getMappingIds()));

        List<EmployeeStockistMapping> existingMappings = employeeStockistMappingRepository.findAllById(mappingIds);
        if (existingMappings.size() != mappingIds.size()) {
            throw new RuntimeException("One or more selected mappings were not found.");
        }

        employeeStockistMappingRepository.deleteAllByIdInBatch(mappingIds);

        return new DeleteStockistMappingsResponseDTO(
                (long) mappingIds.size(),
                mappingIds
        );
    }

    private void validateViewRequest(StockistMappingReportFilterDTO dto) {
        if (dto == null) {
            throw new RuntimeException("Filter payload is required.");
        }

        if (isEmpty(dto.getStateIds()) && isEmpty(dto.getDistrictIds()) && isEmpty(dto.getEmployeeIds())) {
            throw new RuntimeException("At least one filter is required.");
        }
    }

    private boolean isEmpty(List<?> values) {
        return values == null || values.isEmpty();
    }

    private List<Integer> intIdsOrDummy(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of(-1);
        }
        return ids;
    }

    private List<Long> longIdsOrDummy(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of(-1L);
        }
        return ids;
    }
}
