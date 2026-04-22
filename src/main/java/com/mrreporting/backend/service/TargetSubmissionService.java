package com.mrreporting.backend.service;

import com.mrreporting.backend.dto.*;
import com.mrreporting.backend.entity.Employee;
import com.mrreporting.backend.entity.EmployeeTarget;
import com.mrreporting.backend.repository.EmployeeRepository;
import com.mrreporting.backend.repository.EmployeeTargetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class TargetSubmissionService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeTargetRepository employeeTargetRepository;

    @Transactional(readOnly = true)
    public List<DropdownOptionDTO> getActiveMrEmployees(Integer stateId, Integer districtId) {
        if (stateId == null || districtId == null) {
            return List.of();
        }

        return employeeRepository.findActiveMrEmployeesByLocation(stateId, districtId)
                .stream()
                .map(employee -> new DropdownOptionDTO(
                        employee.getId(),
                        employee.getName(),
                        employee.getDesignation() != null ? employee.getDesignation().getName() : "MR"
                ))
                .toList();
    }

    @Transactional
    public TargetSubmissionResponseDTO submitTargets(TargetSubmissionRequestDTO dto) {
        validateRequest(dto);

        List<Long> employeeIds = new ArrayList<>(new LinkedHashSet<>(dto.getEmployeeIds()));
        Map<Integer, BigDecimal> monthlyTargets =
                dto.getMonthlyTargets() != null ? dto.getMonthlyTargets() : Collections.emptyMap();

        validateMonthKeys(monthlyTargets);

        List<Employee> employees = employeeRepository.findActiveMrEmployeesByIds(employeeIds);
        if (employees.size() != employeeIds.size()) {
            throw new RuntimeException("One or more selected employees are invalid or not active MR users.");
        }

        List<EmployeeTarget> existingTargets =
                employeeTargetRepository.findByEmployeeIdInAndYear(employeeIds, dto.getYear());

        Map<String, EmployeeTarget> existingTargetMap = new HashMap<>();
        for (EmployeeTarget target : existingTargets) {
            existingTargetMap.put(buildKey(target.getEmployee().getId(), target.getMonth()), target);
        }

        List<EmployeeTarget> targetsToSave = new ArrayList<>();

        for (Employee employee : employees) {
            for (int month = 1; month <= 12; month++) {
                BigDecimal amount = normalizeAmount(monthlyTargets.get(month), month);

                String key = buildKey(employee.getId(), month);
                EmployeeTarget target = existingTargetMap.get(key);

                if (target == null) {
                    target = new EmployeeTarget();
                    target.setEmployee(employee);
                    target.setYear(dto.getYear());
                    target.setMonth(month);
                }

                target.setTargetAmount(amount);
                targetsToSave.add(target);
            }
        }

        employeeTargetRepository.saveAll(targetsToSave);

        return new TargetSubmissionResponseDTO(
                dto.getYear(),
                (long) employeeIds.size(),
                (long) targetsToSave.size(),
                employeeIds
        );
    }

    private void validateRequest(TargetSubmissionRequestDTO dto) {
        if (dto == null) {
            throw new RuntimeException("Request payload is required.");
        }

        if (dto.getYear() == null || dto.getYear() < 2000 || dto.getYear() > 2100) {
            throw new RuntimeException("Year is invalid.");
        }

        if (dto.getEmployeeIds() == null || dto.getEmployeeIds().isEmpty()) {
            throw new RuntimeException("At least one employee is required.");
        }
    }

    private void validateMonthKeys(Map<Integer, BigDecimal> monthlyTargets) {
        for (Integer month : monthlyTargets.keySet()) {
            if (month == null || month < 1 || month > 12) {
                throw new RuntimeException("Month keys must be between 1 and 12.");
            }
        }
    }

    private BigDecimal normalizeAmount(BigDecimal amount, Integer month) {
        if (amount == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Target amount cannot be negative for month: " + month);
        }

        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    private String buildKey(Long employeeId, Integer month) {
        return employeeId + "-" + month;
    }

    @Transactional(readOnly = true)
    public TargetModifyFormDTO getTargetModifyForm(Long employeeId, Integer year) {
        if (employeeId == null) {
            throw new RuntimeException("Employee is required.");
        }
        if (year == null || year < 2000 || year > 2100) {
            throw new RuntimeException("Year is invalid.");
        }

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));

        if (employee.getIsActive() == null || !employee.getIsActive()) {
            throw new RuntimeException("Inactive employee cannot be used for target modification.");
        }

        if (employee.getDesignation() == null || employee.getDesignation().getName() == null
                || !"MR".equalsIgnoreCase(employee.getDesignation().getName())) {
            throw new RuntimeException("Only MR employees are allowed for target modification.");
        }

        List<EmployeeTarget> targets =
                employeeTargetRepository.findByEmployeeIdAndYearOrderByMonthAsc(employeeId, year);

        Map<Integer, BigDecimal> monthlyTargets = new LinkedHashMap<>();
        for (int month = 1; month <= 12; month++) {
            monthlyTargets.put(month, BigDecimal.ZERO.setScale(2));
        }

        for (EmployeeTarget target : targets) {
            monthlyTargets.put(
                    target.getMonth(),
                    target.getTargetAmount() != null ? target.getTargetAmount() : BigDecimal.ZERO.setScale(2)
            );
        }

        return new TargetModifyFormDTO(
                employee.getId(),
                employee.getName(),
                year,
                monthlyTargets
        );
    }

    @Transactional
    public TargetSubmissionResponseDTO modifyTargets(TargetModifyRequestDTO dto) {
        if (dto == null) {
            throw new RuntimeException("Request payload is required.");
        }

        if (dto.getEmployeeId() == null) {
            throw new RuntimeException("Employee is required.");
        }

        if (dto.getYear() == null || dto.getYear() < 2000 || dto.getYear() > 2100) {
            throw new RuntimeException("Year is invalid.");
        }

        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + dto.getEmployeeId()));

        if (employee.getIsActive() == null || !employee.getIsActive()) {
            throw new RuntimeException("Inactive employee cannot be used for target modification.");
        }

        if (employee.getDesignation() == null || employee.getDesignation().getName() == null
                || !"MR".equalsIgnoreCase(employee.getDesignation().getName())) {
            throw new RuntimeException("Only MR employees are allowed for target modification.");
        }

        Map<Integer, BigDecimal> monthlyTargets =
                dto.getMonthlyTargets() != null ? dto.getMonthlyTargets() : Map.of();

        validateMonthKeys(monthlyTargets);

        List<EmployeeTarget> existingTargets =
                employeeTargetRepository.findByEmployeeIdAndYearOrderByMonthAsc(dto.getEmployeeId(), dto.getYear());

        Map<Integer, EmployeeTarget> existingTargetMap = new HashMap<>();
        for (EmployeeTarget target : existingTargets) {
            existingTargetMap.put(target.getMonth(), target);
        }

        List<EmployeeTarget> targetsToSave = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            BigDecimal amount = normalizeAmount(monthlyTargets.get(month), month);

            EmployeeTarget target = existingTargetMap.get(month);
            if (target == null) {
                target = new EmployeeTarget();
                target.setEmployee(employee);
                target.setYear(dto.getYear());
                target.setMonth(month);
            }

            target.setTargetAmount(amount);
            targetsToSave.add(target);
        }

        employeeTargetRepository.saveAll(targetsToSave);

        return new TargetSubmissionResponseDTO(
                dto.getYear(),
                1L,
                (long) targetsToSave.size(),
                List.of(dto.getEmployeeId())
        );
    }

    @Transactional(readOnly = true)
    public List<DropdownOptionDTO> getActiveMrEmployeesForFilters(
            List<Integer> stateIds,
            List<Integer> districtIds) {

        if (stateIds == null || stateIds.isEmpty() || districtIds == null || districtIds.isEmpty()) {
            return List.of();
        }

        return employeeRepository.findActiveMrEmployeesForTargetReport(
                        false,
                        stateIds,
                        false,
                        districtIds,
                        true,
                        List.of(-1L)
                )
                .stream()
                .map(employee -> new DropdownOptionDTO(
                        employee.getId(),
                        employee.getName(),
                        employee.getDesignation() != null ? employee.getDesignation().getName() : "MR"
                ))
                .toList();
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
    private Map<Integer, BigDecimal> initMonthlyAmountMap() {
        Map<Integer, BigDecimal> monthlyMap = new LinkedHashMap<>();
        for (int month = 1; month <= 12; month++) {
            monthlyMap.put(month, zeroAmount());
        }
        return monthlyMap;
    }
    private BigDecimal zeroAmount() {
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }


    @Transactional(readOnly = true)
    public TargetReportResponseDTO getTargetReport(TargetReportRequestDTO dto) {
        validateTargetReportRequest(dto);

        List<Long> requestedEmployeeIds = new ArrayList<>(new LinkedHashSet<>(dto.getEmployeeIds()));

        List<Employee> employees = employeeRepository.findActiveMrEmployeesForTargetReport(
                isEmpty(dto.getStateIds()),
                intIdsOrDummy(dto.getStateIds()),
                isEmpty(dto.getDistrictIds()),
                intIdsOrDummy(dto.getDistrictIds()),
                isEmpty(requestedEmployeeIds),
                longIdsOrDummy(requestedEmployeeIds)
        );

        if (employees.size() != requestedEmployeeIds.size()) {
            throw new RuntimeException("One or more selected employees are invalid or not active MR users.");
        }

        List<EmployeeTarget> targets =
                employeeTargetRepository.findByEmployeeIdInAndYear(requestedEmployeeIds, dto.getYear());

        Map<String, BigDecimal> targetMap = new HashMap<>();
        for (EmployeeTarget target : targets) {
            targetMap.put(
                    buildKey(target.getEmployee().getId(), target.getMonth()),
                    target.getTargetAmount() != null ? target.getTargetAmount() : zeroAmount()
            );
        }

        Map<Integer, BigDecimal> monthTotals = initMonthlyAmountMap();
        BigDecimal grandTotal = zeroAmount();
        List<TargetReportRowDTO> rows = new ArrayList<>();

        for (Employee employee : employees) {
            Map<Integer, BigDecimal> monthlyTargets = initMonthlyAmountMap();
            BigDecimal rowTotal = zeroAmount();

            for (int month = 1; month <= 12; month++) {
                BigDecimal amount = targetMap.getOrDefault(buildKey(employee.getId(), month), zeroAmount());
                monthlyTargets.put(month, amount);
                monthTotals.put(month, monthTotals.get(month).add(amount));
                rowTotal = rowTotal.add(amount);
            }

            grandTotal = grandTotal.add(rowTotal);

            rows.add(new TargetReportRowDTO(
                    employee.getId(),
                    employee.getName(),
                    monthlyTargets,
                    rowTotal
            ));
        }

        return new TargetReportResponseDTO(
                dto.getYear(),
                rows,
                monthTotals,
                grandTotal,
                (long) rows.size()
        );
    }
    private void validateTargetReportRequest(TargetReportRequestDTO dto) {
        if (dto == null) {
            throw new RuntimeException("Request payload is required.");
        }

        if (dto.getYear() == null || dto.getYear() < 2000 || dto.getYear() > 2100) {
            throw new RuntimeException("Year is invalid.");
        }

        if (dto.getStateIds() == null || dto.getStateIds().isEmpty()) {
            throw new RuntimeException("At least one state is required.");
        }

        if (dto.getDistrictIds() == null || dto.getDistrictIds().isEmpty()) {
            throw new RuntimeException("At least one district is required.");
        }

        if (dto.getEmployeeIds() == null || dto.getEmployeeIds().isEmpty()) {
            throw new RuntimeException("At least one employee is required.");
        }
    }


}
