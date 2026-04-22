package com.mrreporting.backend.service;

import com.mrreporting.backend.dto.StockistMappingEmployeeRowDTO;
import com.mrreporting.backend.dto.StockistMappingRequestDTO;
import com.mrreporting.backend.dto.StockistMappingResponseDTO;
import com.mrreporting.backend.dto.StockistMappingStockistRowDTO;
import com.mrreporting.backend.entity.Employee;
import com.mrreporting.backend.entity.EmployeeStockistMapping;
import com.mrreporting.backend.entity.Provider;
import com.mrreporting.backend.repository.EmployeeRepository;
import com.mrreporting.backend.repository.EmployeeStockistMappingRepository;
import com.mrreporting.backend.repository.ProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StockistMappingService {

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeStockistMappingRepository employeeStockistMappingRepository;

    public List<StockistMappingStockistRowDTO> getStockistsForMapping(
            Integer stateId,
            List<Integer> districtIds) {

        if (stateId == null || districtIds == null || districtIds.isEmpty()) {
            return List.of();
        }

        List<Provider> stockists =
                providerRepository.findByStateIdAndDistrictIdInAndTypeIgnoreCaseAndIsActiveTrueOrderByProviderNameAsc(
                        stateId,
                        districtIds,
                        "Stockist"
                );

        if (stockists.isEmpty()) {
            return List.of();
        }

        List<Long> stockistIds = stockists.stream()
                .map(Provider::getId)
                .toList();

        Map<Long, Long> mappedCounts = employeeStockistMappingRepository.findByStockistIdIn(stockistIds)
                .stream()
                .collect(Collectors.groupingBy(
                        mapping -> mapping.getStockist().getId(),
                        Collectors.counting()
                ));

        return stockists.stream()
                .map(stockist -> new StockistMappingStockistRowDTO(
                        stockist.getId(),
                        stockist.getDistrict() != null ? stockist.getDistrict().getDistrictName() : "",
                        stockist.getProviderName(),
                        stockist.getEmployee() != null ? stockist.getEmployee().getName() : "",
                        mappedCounts.getOrDefault(stockist.getId(), 0L)
                ))
                .toList();
    }

    public List<StockistMappingEmployeeRowDTO> getEmployeesForMapping(
            Integer stateId,
            List<Integer> districtIds,
            Long stockistId) {

        if (stateId == null || districtIds == null || districtIds.isEmpty()) {
            return List.of();
        }

        final Set<Long> mappedEmployeeIds;

        if (stockistId != null) {
            Provider stockist = providerRepository.findByIdAndTypeIgnoreCaseAndIsActiveTrue(stockistId, "Stockist")
                    .orElseThrow(() -> new RuntimeException("Active stockist not found with id: " + stockistId));

            mappedEmployeeIds = employeeStockistMappingRepository.findByStockistId(stockist.getId())
                    .stream()
                    .map(mapping -> mapping.getEmployee().getId())
                    .collect(Collectors.toSet());
        } else {
            mappedEmployeeIds = Collections.emptySet();
        }

        List<Employee> employees =
                employeeRepository.findByStateIdAndDistrictIdInAndIsActiveTrueOrderByNameAsc(stateId, districtIds);

        return employees.stream()
                .map(employee -> new StockistMappingEmployeeRowDTO(
                        employee.getId(),
                        employee.getDistrict() != null ? employee.getDistrict().getDistrictName() : "",
                        employee.getName(),
                        employee.getDesignation() != null ? employee.getDesignation().getName() : "",
                        mappedEmployeeIds.contains(employee.getId())
                ))
                .toList();
    }


    @Transactional
    public StockistMappingResponseDTO mapStockistToEmployees(StockistMappingRequestDTO dto) {
        if (dto.getStockistId() == null) {
            throw new RuntimeException("Stockist is required.");
        }

        Provider stockist = providerRepository.findByIdAndTypeIgnoreCaseAndIsActiveTrue(dto.getStockistId(), "Stockist")
                .orElseThrow(() -> new RuntimeException("Active stockist not found with id: " + dto.getStockistId()));

        List<Long> requestedEmployeeIds = dto.getEmployeeIds() == null
                ? List.of()
                : new ArrayList<>(new LinkedHashSet<>(dto.getEmployeeIds()));

        List<Employee> employees = requestedEmployeeIds.isEmpty()
                ? List.of()
                : employeeRepository.findAllById(requestedEmployeeIds);

        if (employees.size() != requestedEmployeeIds.size()) {
            throw new RuntimeException("One or more employees not found.");
        }

        for (Employee employee : employees) {
            if (employee.getIsActive() == null || !employee.getIsActive()) {
                throw new RuntimeException("Inactive employees cannot be mapped to stockists.");
            }
        }

        employeeStockistMappingRepository.deleteByStockistId(stockist.getId());

        List<EmployeeStockistMapping> mappingsToSave = new ArrayList<>();
        for (Employee employee : employees) {
            EmployeeStockistMapping mapping = new EmployeeStockistMapping();
            mapping.setStockist(stockist);
            mapping.setEmployee(employee);
            mappingsToSave.add(mapping);
        }

        if (!mappingsToSave.isEmpty()) {
            employeeStockistMappingRepository.saveAll(mappingsToSave);
        }

        return new StockistMappingResponseDTO(
                stockist.getId(),
                stockist.getProviderName(),
                (long) mappingsToSave.size(),
                requestedEmployeeIds
        );
    }
}
