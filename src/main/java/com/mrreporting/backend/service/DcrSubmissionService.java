package com.mrreporting.backend.service;

import com.mrreporting.backend.dto.*;
import com.mrreporting.backend.entity.*;
import com.mrreporting.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Service
public class DcrSubmissionService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private DcrReportRepository dcrReportRepository;

    @Transactional(readOnly = true)
    public List<DropdownOptionDTO> getManagers() {
        return employeeRepository.findActiveManagersForDcr()
                .stream()
                .map(employee -> new DropdownOptionDTO(
                        employee.getId(),
                        employee.getName(),
                        employee.getDesignation() != null ? employee.getDesignation().getName() : null
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DropdownOptionDTO> getAreasForCurrentMr() {
        Employee employee = getCurrentMr();

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
    public List<DropdownOptionDTO> getMappedDoctorsForCurrentMr() {
        Employee employee = getCurrentMr();

        return doctorRepository.findByEmployeeIdAndIsActiveTrueOrderByDoctorNameAsc(employee.getId())
                .stream()
                .map(doctor -> new DropdownOptionDTO(
                        doctor.getId(),
                        doctor.getDoctorName(),
                        doctor.getMslNo()
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

    @Transactional(readOnly = true)
    public List<DropdownOptionDTO> getChemistsAndStockistsForCurrentMr() {
        Employee employee = getCurrentMr();

        return providerRepository.findByEmployeeIdAndIsActiveTrueOrderByProviderNameAsc(employee.getId())
                .stream()
                .filter(provider -> isChemistOrStockist(provider.getType()))
                .map(provider -> new DropdownOptionDTO(
                        provider.getId(),
                        provider.getProviderName(),
                        provider.getType()
                ))
                .toList();
    }

    @Transactional
    public DcrSubmitResponseDTO submitDcr(DcrSubmitRequestDTO dto) {
        Employee employee = getCurrentMr();
        validateSubmitRequest(dto, employee);

        Employee topJointManager = resolveManager(dto.getJointWorkManagerId(), false);

        DcrReport report = new DcrReport();
        report.setEmployee(employee);
        report.setReportDate(dto.getDcrDate());
        report.setWorkingStatus(clean(dto.getWorkingStatus()));
        report.setJointWorkManager(topJointManager);
        report.setJointWorkWith(topJointManager != null ? topJointManager.getName() : null);
        report.setRemark(cleanNullable(dto.getRemarks()));
        report.setReportedFrom("WEB");
        report.setSubmittedAt(LocalDateTime.now());
        report.setIsDelayed(false);
        report.setIsDeviated(Boolean.TRUE.equals(dto.getIsDeviate()));
        report.setDeviateReason(Boolean.TRUE.equals(dto.getIsDeviate()) ? clean(dto.getDeviateReason()) : null);

        long areaCount = appendTravelAreas(report, employee, dto.getTravelAreas());
        if (requiresTravelAreas(dto.getWorkingStatus()) && areaCount == 0L) {
            throw new RuntimeException("At least one travel area is required for working status: " + dto.getWorkingStatus());
        }
        long doctorCallCount = appendDoctorCalls(report, employee, dto.getDoctorCalls(), topJointManager);
        long chemistStockistCallCount = appendChemistStockistCalls(report, employee, dto.getChemistStockistCalls(), topJointManager);
        long meetingCount = appendMeetings(report, dto.getNextMeetings());
        long expenseCount = appendExpenses(report, dto.getExpenses());

        DcrReport savedReport = dcrReportRepository.save(report);

        return new DcrSubmitResponseDTO(
                savedReport.getId(),
                employee.getId(),
                employee.getName(),
                savedReport.getReportDate(),
                savedReport.getWorkingStatus(),
                areaCount,
                doctorCallCount,
                chemistStockistCallCount,
                meetingCount,
                expenseCount
        );
    }

    private long appendTravelAreas(DcrReport report, Employee employee, List<DcrAreaPairDTO> travelAreas) {
        if (travelAreas == null || travelAreas.isEmpty()) {
            return 0L;
        }

        int index = 0;
        for (DcrAreaPairDTO pair : travelAreas) {
            if (isEmptyAreaPair(pair)) {
                continue;
            }
            if (pair.getFromAreaId() == null || pair.getToAreaId() == null) {
                throw new RuntimeException("Both fromAreaId and toAreaId are required for each travel area row.");
            }

            Area fromArea = areaRepository.findById(pair.getFromAreaId())
                    .orElseThrow(() -> new RuntimeException("From area not found with id: " + pair.getFromAreaId()));
            Area toArea = areaRepository.findById(pair.getToAreaId())
                    .orElseThrow(() -> new RuntimeException("To area not found with id: " + pair.getToAreaId()));

            validateAreaBelongsToMr(fromArea, employee, "fromAreaId");
            validateAreaBelongsToMr(toArea, employee, "toAreaId");

            DcrReportArea travelArea = new DcrReportArea();
            travelArea.setFromArea(fromArea);
            travelArea.setToArea(toArea);
            travelArea.setSortOrder(index++);
            travelArea.setIsActive(true);
            report.addTravelArea(travelArea);
        }

        return report.getTravelAreas().size();
    }

    private long appendDoctorCalls(
            DcrReport report,
            Employee employee,
            List<DcrDoctorCallDTO> doctorCalls,
            Employee topJointManager
    ) {
        if (doctorCalls == null || doctorCalls.isEmpty()) {
            return 0L;
        }

        int index = 0;
        for (DcrDoctorCallDTO dto : doctorCalls) {
            if (isEmptyDoctorCall(dto)) {
                continue;
            }

            if (dto.getDoctorId() == null) {
                throw new RuntimeException("doctorId is required for each doctor call.");
            }

            Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                    .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + dto.getDoctorId()));
            validateDoctorBelongsToMr(doctor, employee);

            List<Long> productIds = uniqueIds(dto.getProductListIds());

            Employee jointManager = dto.getJointWithManagerId() != null
                    ? resolveManager(dto.getJointWithManagerId(), false)
                    : topJointManager;

            DcrReportCall call = new DcrReportCall();
            call.setDoctor(doctor);
            call.setCallType("DOCTOR");
            call.setPartyName(doctor.getDoctorName());
            call.setIsListed(true);
            call.setIsInPerson(true);
            call.setPobAmount(BigDecimal.ZERO);
            call.setCallTime(null);
            call.setRemark(cleanNullable(dto.getRemarks()));
            call.setJointWorkManager(jointManager);
            call.setSortOrder(index++);
            call.setIsActive(true);

            for (Long productId : productIds) {
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

                DcrReportCallProduct callProduct = new DcrReportCallProduct();
                callProduct.setProduct(product);
                callProduct.setIsActive(true);
                call.addProduct(callProduct);
            }

            report.addCall(call);
        }

        return report.getCalls().stream()
                .filter(call -> "DOCTOR".equalsIgnoreCase(call.getCallType()))
                .count();
    }

    private long appendChemistStockistCalls(
            DcrReport report,
            Employee employee,
            List<DcrChemistStockistCallDTO> calls,
            Employee topJointManager
    ) {
        if (calls == null || calls.isEmpty()) {
            return 0L;
        }

        int index = report.getCalls().size();
        for (DcrChemistStockistCallDTO dto : calls) {
            if (isEmptyChemistStockistCall(dto)) {
                continue;
            }

            if (dto.getChemistStockistId() == null) {
                throw new RuntimeException("chemistStockistId is required for each chemist/stockist call.");
            }

            Provider provider = providerRepository.findById(dto.getChemistStockistId())
                    .orElseThrow(() -> new RuntimeException("Chemist/Stockist not found with id: " + dto.getChemistStockistId()));
            String callType = isBlank(dto.getType())
                    ? normalizeProviderType(provider.getType())
                    : normalizeProviderType(dto.getType());
            validateProviderBelongsToMr(provider, employee, callType);

            Employee jointManager = dto.getJointWithManagerId() != null
                    ? resolveManager(dto.getJointWithManagerId(), false)
                    : topJointManager;

            DcrReportCall call = new DcrReportCall();
            call.setProvider(provider);
            call.setCallType(callType);
            call.setPartyName(provider.getProviderName());
            call.setIsListed(true);
            call.setIsInPerson(true);
            call.setPobAmount(BigDecimal.ZERO);
            call.setCallTime(null);
            call.setRemark(cleanNullable(dto.getRemarks()));
            call.setJointWorkManager(jointManager);
            call.setSortOrder(index++);
            call.setIsActive(true);
            report.addCall(call);
        }

        return report.getCalls().stream()
                .filter(call -> "CHEMIST".equalsIgnoreCase(call.getCallType()) || "STOCKIST".equalsIgnoreCase(call.getCallType()))
                .count();
    }

    private long appendMeetings(DcrReport report, List<DcrNextMeetingDTO> meetings) {
        if (meetings == null || meetings.isEmpty()) {
            return 0L;
        }

        int index = 0;
        for (DcrNextMeetingDTO dto : meetings) {
            if (isEmptyMeeting(dto)) {
                continue;
            }

            Employee manager = resolveManager(dto.getMeetingWithManagerId(), true);
            if (isBlank(dto.getSubject())) {
                throw new RuntimeException("Meeting subject is required.");
            }

            DcrReportMeeting meeting = new DcrReportMeeting();
            meeting.setMeetingWithManager(manager);
            meeting.setSubject(clean(dto.getSubject()));
            meeting.setRemark(cleanNullable(dto.getRemarks()));
            meeting.setSortOrder(index++);
            meeting.setIsActive(true);
            report.addMeeting(meeting);
        }

        return report.getMeetings().size();
    }

    private long appendExpenses(DcrReport report, List<DcrExpenseItemDTO> expenses) {
        if (expenses == null || expenses.isEmpty()) {
            return 0L;
        }

        int index = 0;
        for (DcrExpenseItemDTO dto : expenses) {
            if (isEmptyExpense(dto)) {
                continue;
            }

            if (isBlank(dto.getExpenseType())) {
                throw new RuntimeException("Expense type is required.");
            }
            if (dto.getAmount() == null) {
                throw new RuntimeException("Expense amount is required.");
            }
            if (dto.getAmount().compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("Expense amount cannot be negative.");
            }

            DcrReportExpense expense = new DcrReportExpense();
            expense.setExpenseType(clean(dto.getExpenseType()));
            expense.setAmount(dto.getAmount());
            expense.setRemark(cleanNullable(dto.getRemarks()));
            expense.setSortOrder(index++);
            expense.setIsActive(true);
            report.addExpense(expense);
        }

        return report.getExpenses().size();
    }

    private void validateSubmitRequest(DcrSubmitRequestDTO dto, Employee employee) {
        if (dto == null) {
            throw new RuntimeException("DCR payload is required.");
        }
        if (dto.getDcrDate() == null) {
            throw new RuntimeException("dcrDate is required.");
        }
        if (isBlank(dto.getWorkingStatus())) {
            throw new RuntimeException("workingStatus is required.");
        }
        if (dcrReportRepository.existsByEmployeeIdAndReportDate(employee.getId(), dto.getDcrDate())) {
            throw new RuntimeException("A DCR is already submitted for this date.");
        }
        if (Boolean.TRUE.equals(dto.getIsDeviate()) && isBlank(dto.getDeviateReason())) {
            throw new RuntimeException("deviateReason is required when isDeviate is true.");
        }
        if (requiresTravelAreas(dto.getWorkingStatus()) && (dto.getTravelAreas() == null || dto.getTravelAreas().isEmpty())) {
            throw new RuntimeException("At least one travel area is required for working status: " + dto.getWorkingStatus());
        }
    }

    private Employee getCurrentMr() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User currentUser)) {
            throw new RuntimeException("Unauthorized user.");
        }

        Employee employee = employeeRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Employee profile not found for the logged-in user."));

        if (!Boolean.TRUE.equals(employee.getIsActive())) {
            throw new RuntimeException("Inactive employees cannot submit DCR.");
        }
        if (employee.getDesignation() == null || !"MR".equalsIgnoreCase(cleanNullable(employee.getDesignation().getName()))) {
            throw new RuntimeException("Only MR users can submit DCR.");
        }

        return employee;
    }

    private Employee resolveManager(Long managerId, boolean required) {
        if (managerId == null) {
            if (required) {
                throw new RuntimeException("Manager selection is required.");
            }
            return null;
        }

        Employee manager = employeeRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Manager not found with id: " + managerId));

        if (!Boolean.TRUE.equals(manager.getIsActive())) {
            throw new RuntimeException("Inactive manager cannot be selected.");
        }
        if (manager.getDesignation() == null || manager.getDesignation().getLevel() == null || manager.getDesignation().getLevel() <= 1) {
            throw new RuntimeException("Selected employee is not a valid manager.");
        }
        if ("ADMIN".equalsIgnoreCase(cleanNullable(manager.getDesignation().getName()))) {
            throw new RuntimeException("Admin cannot be selected for DCR manager fields.");
        }

        return manager;
    }

    private void validateAreaBelongsToMr(Area area, Employee employee, String fieldName) {
        if (!Boolean.TRUE.equals(area.getIsActive())) {
            throw new RuntimeException("Inactive area cannot be used for " + fieldName + ".");
        }
        if (area.getEmployee() == null || !employee.getId().equals(area.getEmployee().getId())) {
            throw new RuntimeException("Selected area does not belong to the logged-in MR for " + fieldName + ".");
        }
    }

    private void validateDoctorBelongsToMr(Doctor doctor, Employee employee) {
        if (!Boolean.TRUE.equals(doctor.getIsActive())) {
            throw new RuntimeException("Inactive doctor cannot be used in DCR.");
        }
        if (doctor.getEmployee() == null || !employee.getId().equals(doctor.getEmployee().getId())) {
            throw new RuntimeException("Selected doctor does not belong to the logged-in MR.");
        }
    }

    private void validateProviderBelongsToMr(Provider provider, Employee employee, String expectedType) {
        if (!Boolean.TRUE.equals(provider.getIsActive())) {
            throw new RuntimeException("Inactive provider cannot be used in DCR.");
        }
        if (provider.getEmployee() == null || !employee.getId().equals(provider.getEmployee().getId())) {
            throw new RuntimeException("Selected provider does not belong to the logged-in MR.");
        }
        if (!expectedType.equalsIgnoreCase(cleanNullable(provider.getType()))) {
            throw new RuntimeException("Selected provider type does not match " + expectedType + ".");
        }
    }

    private boolean requiresTravelAreas(String workingStatus) {
        String normalized = clean(workingStatus).toUpperCase();
        return "WORKING".equals(normalized) || "FIELD WORK".equals(normalized);
    }

    private boolean isChemistOrStockist(String type) {
        String normalized = type == null ? "" : type.trim().toUpperCase();
        return "CHEMIST".equals(normalized) || "STOCKIST".equals(normalized);
    }

    private boolean isEmptyAreaPair(DcrAreaPairDTO dto) {
        return dto == null || (dto.getFromAreaId() == null && dto.getToAreaId() == null);
    }

    private boolean isEmptyDoctorCall(DcrDoctorCallDTO dto) {
        return dto == null
                || (dto.getDoctorId() == null
                && dto.getJointWithManagerId() == null
                && isBlank(dto.getRemarks())
                && uniqueIds(dto.getProductListIds()).isEmpty());
    }

    private boolean isEmptyChemistStockistCall(DcrChemistStockistCallDTO dto) {
        return dto == null
                || (dto.getChemistStockistId() == null
                && dto.getJointWithManagerId() == null
                && isBlank(dto.getType())
                && isBlank(dto.getRemarks()));
    }

    private boolean isEmptyMeeting(DcrNextMeetingDTO dto) {
        return dto == null
                || (dto.getMeetingWithManagerId() == null
                && isBlank(dto.getSubject())
                && isBlank(dto.getRemarks()));
    }

    private boolean isEmptyExpense(DcrExpenseItemDTO dto) {
        return dto == null
                || (isBlank(dto.getExpenseType())
                && dto.getAmount() == null
                && isBlank(dto.getRemarks()));
    }

    private String normalizeProviderType(String type) {
        if (isBlank(type)) {
            throw new RuntimeException("type is required for chemist/stockist call.");
        }

        String normalized = clean(type).toUpperCase();
        if (!"CHEMIST".equals(normalized) && !"STOCKIST".equals(normalized)) {
            throw new RuntimeException("type must be CHEMIST or STOCKIST.");
        }
        return normalized;
    }

    private List<Long> uniqueIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return new ArrayList<>(new LinkedHashSet<>(ids));
    }

    private String clean(String value) {
        return value.trim();
    }

    private String cleanNullable(String value) {
        return value == null ? null : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
