package com.mrreporting.backend.service;

import com.mrreporting.backend.dto.*;
import com.mrreporting.backend.entity.DcrReport;
import com.mrreporting.backend.entity.DcrReportCall;
import com.mrreporting.backend.entity.Employee;
import com.mrreporting.backend.repository.DcrReportRepository;
import com.mrreporting.backend.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class DcrConsolidateService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DcrReportRepository dcrReportRepository;

    @Transactional(readOnly = true)
    public DcrConsolidateResponseDTO getConsolidateSummary(DcrConsolidateFilterDTO dto) {
        validateFilter(dto);

        List<Employee> employees = resolveEmployees(dto);
        if (employees.isEmpty()) {
            return new DcrConsolidateResponseDTO(List.of(), 0L);
        }

        List<Long> employeeIds = employees.stream()
                .map(Employee::getId)
                .toList();

        List<DcrReport> reports = dcrReportRepository.findByEmployeeIdsAndDateRangeWithCalls(
                employeeIds,
                dto.getFromDate(),
                dto.getToDate()
        );
        reports = reports.stream()
                .sorted(Comparator
                        .comparing((DcrReport report) -> report.getEmployee().getName(), String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(DcrReport::getReportDate))
                .toList();

        Map<Long, List<DcrReport>> reportsByEmployee = reports.stream()
                .collect(Collectors.groupingBy(report -> report.getEmployee().getId()));

        List<DcrConsolidateRowDTO> rows = new ArrayList<>();
        for (Employee employee : employees) {
            List<DcrReport> employeeReports = reportsByEmployee.getOrDefault(employee.getId(), List.of());
            if (employeeReports.isEmpty()) {
                continue;
            }
            rows.add(buildConsolidateRow(employee, employeeReports));
        }

        return new DcrConsolidateResponseDTO(
                rows,
                (long) rows.size()
        );
    }

    @Transactional(readOnly = true)
    public DcrDateWiseResponseDTO getEmployeeDateWiseReport(
            Long employeeId,
            LocalDate fromDate,
            LocalDate toDate
    ) {
        if (employeeId == null) {
            throw new RuntimeException("Employee is required.");
        }
        if (fromDate == null || toDate == null) {
            throw new RuntimeException("Both fromDate and toDate are required.");
        }
        if (fromDate.isAfter(toDate)) {
            throw new RuntimeException("fromDate cannot be after toDate.");
        }

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));

        List<DcrReport> reports = dcrReportRepository.findByEmployeeIdAndDateRangeWithCalls(
                employeeId,
                fromDate,
                toDate
        );
        reports = reports.stream()
                .sorted(Comparator.comparing(DcrReport::getReportDate))
                .toList();

        DcrDateWiseSummaryDTO summary = buildDateWiseSummary(reports);

        List<DcrDateWiseRowDTO> rows = reports.stream()
                .map(report -> new DcrDateWiseRowDTO(
                        report.getId(),
                        report.getReportDate(),
                        employee.getName(),
                        employee.getReportingManager() != null ? employee.getReportingManager().getName() : "-",
                        nullSafeString(report.getWorkingStatus()),
                        nullSafeString(report.getReportedFrom()),
                        report.getDayStartTime(),
                        report.getDayEndTime(),
                        report.getFirstCallTime(),
                        report.getLastCallTime(),
                        Boolean.TRUE.equals(report.getIsDelayed()),
                        Boolean.TRUE.equals(report.getIsDeviated()),
                        blankToDash(report.getJointWorkWith()),
                        countCalls(report, this::isDoctorCall),
                        countCalls(report, this::isChemistCall),
                        countCalls(report, this::isStockistCall),
                        sumPob(report.getCalls())
                ))
                .toList();

        return new DcrDateWiseResponseDTO(
                employee.getId(),
                employee.getName(),
                employee.getReportingManager() != null ? employee.getReportingManager().getName() : "-",
                summary,
                rows,
                (long) rows.size()
        );
    }

    private List<Employee> resolveEmployees(DcrConsolidateFilterDTO dto) {
        Boolean status = parseStatus(dto.getStatus());
        List<Integer> stateIds = dto.getStateIds();
        List<Integer> districtIds = dto.getDistrictIds();

        if ("HIERARCHICAL".equalsIgnoreCase(dto.getFilterMode())) {
            return employeeRepository.findEmployeesForDcrConsolidateHierarchical(
                    status,
                    isEmpty(stateIds),
                    intIdsOrDummy(stateIds),
                    isEmpty(districtIds),
                    intIdsOrDummy(districtIds)
            );
        }

        return employeeRepository.findEmployeesForDcrConsolidateGeographical(
                status,
                isEmpty(stateIds),
                intIdsOrDummy(stateIds),
                isEmpty(districtIds),
                intIdsOrDummy(districtIds)
        );
    }

    private DcrConsolidateRowDTO buildConsolidateRow(Employee employee, List<DcrReport> reports) {
        long inPersonDoctorCount = 0L;
        long chemistMetCount = 0L;
        long stockistMetCount = 0L;
        BigDecimal doctorPob = zero();
        BigDecimal totalPob = zero();
        LocalDateTime lastSubmittedDcr = null;

        for (DcrReport report : reports) {
            if (report.getSubmittedAt() != null &&
                    (lastSubmittedDcr == null || report.getSubmittedAt().isAfter(lastSubmittedDcr))) {
                lastSubmittedDcr = report.getSubmittedAt();
            }

            for (DcrReportCall call : safeCalls(report)) {
                BigDecimal pobAmount = nullSafePob(call.getPobAmount());
                totalPob = totalPob.add(pobAmount);

                if (isDoctorCall(call)) {
                    doctorPob = doctorPob.add(pobAmount);
                    if (Boolean.TRUE.equals(call.getIsInPerson())) {
                        inPersonDoctorCount++;
                    }
                } else if (isChemistCall(call)) {
                    chemistMetCount++;
                } else if (isStockistCall(call)) {
                    stockistMetCount++;
                }
            }
        }

        long totalProviderMet = reports.stream()
                .flatMap(report -> safeCalls(report).stream())
                .count();

        return new DcrConsolidateRowDTO(
                employee.getId(),
                employee.getDistrict() != null ? employee.getDistrict().getDistrictName() : "-",
                nullSafeString(employee.getUserCode()),
                employee.getName(),
                employee.getReportingManager() != null ? employee.getReportingManager().getName() : "-",
                employee.getDesignation() != null ? employee.getDesignation().getName() : "-",
                employee.getDateOfJoining(),
                employee.getDateOfConfirmation(),
                nullSafeString(employee.getMobile()),
                lastSubmittedDcr,
                (long) reports.size(),
                inPersonDoctorCount,
                doctorPob,
                chemistMetCount,
                stockistMetCount,
                totalProviderMet,
                totalPob
        );
    }

    private DcrDateWiseSummaryDTO buildDateWiseSummary(List<DcrReport> reports) {
        long adminWorkCount = countReportsByStatus(reports, "Admin Work");
        long meetingCount = countReportsByStatus(reports, "Meeting");
        long holidayCount = countReportsByStatus(reports, "Holiday");
        long fieldWorkCount = countReportsByStatus(reports, "Field Work");
        long leaveCount = countReportsByStatus(reports, "Leave");
        long lwpCount = countReportsByStatus(reports, "LWP");
        long conferenceCount = countReportsByStatus(reports, "Conference");
        long chemistWorkCount = countReportsByStatus(reports, "Chemist Work");
        long stockistWorkCount = countReportsByStatus(reports, "Stockist Work");
        long transitCount = countReportsByStatus(reports, "Transit");
        long otherCount = countReportsByStatus(reports, "Other");

        List<DcrReportCall> allCalls = reports.stream()
                .flatMap(report -> safeCalls(report).stream())
                .toList();

        long totalDoctorMet = allCalls.stream().filter(this::isDoctorCall).count();
        long totalChemistMet = allCalls.stream().filter(this::isChemistCall).count();
        long totalStockistMet = allCalls.stream().filter(this::isStockistCall).count();

        long unlistedDoctorMet = allCalls.stream().filter(call -> isDoctorCall(call) && !Boolean.TRUE.equals(call.getIsListed())).count();
        long unlistedChemistMet = allCalls.stream().filter(call -> isChemistCall(call) && !Boolean.TRUE.equals(call.getIsListed())).count();
        long unlistedStockistMet = allCalls.stream().filter(call -> isStockistCall(call) && !Boolean.TRUE.equals(call.getIsListed())).count();

        BigDecimal unlistedDoctorPob = sumPob(allCalls.stream()
                .filter(call -> isDoctorCall(call) && !Boolean.TRUE.equals(call.getIsListed()))
                .toList());
        BigDecimal unlistedChemistPob = sumPob(allCalls.stream()
                .filter(call -> isChemistCall(call) && !Boolean.TRUE.equals(call.getIsListed()))
                .toList());
        BigDecimal unlistedStockistPob = sumPob(allCalls.stream()
                .filter(call -> isStockistCall(call) && !Boolean.TRUE.equals(call.getIsListed()))
                .toList());

        BigDecimal doctorPobValue = sumPob(allCalls.stream().filter(this::isDoctorCall).toList());
        BigDecimal chemistPobValue = sumPob(allCalls.stream().filter(this::isChemistCall).toList());
        BigDecimal stockistPobValue = sumPob(allCalls.stream().filter(this::isStockistCall).toList());
        BigDecimal combinedDoctorChemistPobValue = doctorPobValue.add(chemistPobValue);
        BigDecimal totalPobValue = combinedDoctorChemistPobValue.add(stockistPobValue);

        long tpDeviationCount = reports.stream().filter(report -> Boolean.TRUE.equals(report.getIsDeviated())).count();
        long jointWorkDays = reports.stream().filter(report -> report.getJointWorkWith() != null && !report.getJointWorkWith().isBlank()).count();

        return new DcrDateWiseSummaryDTO(
                adminWorkCount,
                meetingCount,
                holidayCount,
                fieldWorkCount,
                leaveCount,
                lwpCount,
                conferenceCount,
                chemistWorkCount,
                stockistWorkCount,
                transitCount,
                otherCount,
                (long) reports.size(),
                totalDoctorMet,
                totalChemistMet,
                totalStockistMet,
                unlistedDoctorPob,
                unlistedChemistPob,
                unlistedStockistPob,
                unlistedDoctorMet,
                unlistedChemistMet,
                unlistedStockistMet,
                average(totalDoctorMet, fieldWorkCount),
                average(totalChemistMet, fieldWorkCount),
                average(totalStockistMet, fieldWorkCount),
                average(unlistedDoctorMet, fieldWorkCount),
                average(unlistedChemistMet, fieldWorkCount),
                average(unlistedStockistMet, fieldWorkCount),
                tpDeviationCount,
                jointWorkDays,
                average(jointWorkDays, fieldWorkCount),
                doctorPobValue,
                chemistPobValue,
                stockistPobValue,
                combinedDoctorChemistPobValue,
                totalPobValue
        );
    }

    private long countReportsByStatus(List<DcrReport> reports, String status) {
        return reports.stream()
                .filter(report -> status.equalsIgnoreCase(nullSafeString(report.getWorkingStatus())))
                .count();
    }

    private long countCalls(DcrReport report, Predicate<DcrReportCall> predicate) {
        return safeCalls(report).stream()
                .filter(predicate)
                .count();
    }

    private boolean isDoctorCall(DcrReportCall call) {
        return "DOCTOR".equalsIgnoreCase(nullSafeString(call.getCallType()));
    }

    private boolean isChemistCall(DcrReportCall call) {
        return "CHEMIST".equalsIgnoreCase(nullSafeString(call.getCallType()));
    }

    private boolean isStockistCall(DcrReportCall call) {
        return "STOCKIST".equalsIgnoreCase(nullSafeString(call.getCallType()));
    }

    private BigDecimal sumPob(List<DcrReportCall> calls) {
        BigDecimal total = zero();
        for (DcrReportCall call : calls) {
            total = total.add(nullSafePob(call.getPobAmount()));
        }
        return total;
    }

    private BigDecimal average(long numerator, long denominator) {
        if (denominator <= 0) {
            return zero();
        }
        return BigDecimal.valueOf(numerator)
                .divide(BigDecimal.valueOf(denominator), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal zero() {
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal nullSafePob(BigDecimal value) {
        return value != null ? value.setScale(2, RoundingMode.HALF_UP) : zero();
    }

    private List<DcrReportCall> safeCalls(DcrReport report) {
        return report.getCalls() != null ? report.getCalls() : List.of();
    }

    private void validateFilter(DcrConsolidateFilterDTO dto) {
        if (dto == null) {
            throw new RuntimeException("Filter payload is required.");
        }
        if (dto.getFromDate() == null || dto.getToDate() == null) {
            throw new RuntimeException("Both fromDate and toDate are required.");
        }
        if (dto.getFromDate().isAfter(dto.getToDate())) {
            throw new RuntimeException("fromDate cannot be after toDate.");
        }
        if (isEmpty(dto.getStateIds())) {
            throw new RuntimeException("At least one state is required.");
        }
        if (isEmpty(dto.getDistrictIds())) {
            throw new RuntimeException("At least one district is required.");
        }
    }

    private Boolean parseStatus(String status) {
        if (status == null || status.isBlank() || "ALL".equalsIgnoreCase(status)) {
            return null;
        }
        if ("ACTIVE".equalsIgnoreCase(status)) {
            return true;
        }
        if ("INACTIVE".equalsIgnoreCase(status)) {
            return false;
        }
        throw new RuntimeException("Unsupported status filter: " + status);
    }

    private boolean isEmpty(List<?> values) {
        return values == null || values.isEmpty();
    }

    private List<Integer> intIdsOrDummy(List<Integer> ids) {
        return isEmpty(ids) ? List.of(-1) : ids;
    }

    private String nullSafeString(String value) {
        return value != null ? value : "";
    }

    private String blankToDash(String value) {
        return value != null && !value.isBlank() ? value : "-";
    }
}
