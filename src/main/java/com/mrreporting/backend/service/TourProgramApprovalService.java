package com.mrreporting.backend.service;

import com.mrreporting.backend.dto.TourProgramDayDetailDTO;
import com.mrreporting.backend.dto.TourProgramSummaryDTO;
import com.mrreporting.backend.entity.Employee;
import com.mrreporting.backend.entity.TourProgram;
import com.mrreporting.backend.entity.TourProgramDay;
import com.mrreporting.backend.entity.TourProgramDayArea;
import com.mrreporting.backend.repository.EmployeeRepository;
import com.mrreporting.backend.repository.TourProgramDayRepository;
import com.mrreporting.backend.repository.TourProgramRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TourProgramApprovalService {

    @Autowired
    private TourProgramRepository tourProgramRepository;

    @Autowired
    private TourProgramDayRepository tourProgramDayRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    // geographical filter: find employees by district, then get their tour program status
    public List<TourProgramSummaryDTO> getGeographicalSummary(
            List<Integer> districtIds,
            Integer month,
            Integer year) {

        if (districtIds == null || districtIds.isEmpty()) {
            throw new IllegalArgumentException("At least one district must be selected.");
        }

        List<Employee> employees = employeeRepository.findByDistrictIdIn(districtIds);
        return buildSummary(employees, month, year);
    }

    // hierarchical filter: find employees by designation and status, then get their tour program status.
    // if specific employeeIds are passed, we filter down further to just those employees.
    public List<TourProgramSummaryDTO> getHierarchicalSummary(
            Long designationId,
            Boolean isActive,
            List<Long> employeeIds,
            Integer month,
            Integer year) {

        if (designationId == null) {
            throw new IllegalArgumentException("Designation is required.");
        }

        List<Employee> employees;

        if (employeeIds != null && !employeeIds.isEmpty()) {
            // admin picked specific employees from the dropdown
            employees = employeeRepository.findByIdInAndDesignationIdAndIsActive(
                    employeeIds, designationId, isActive);
        } else {
            // no specific employees selected, return all matching designation + status
            employees = employeeRepository.findByDesignationIdAndIsActive(designationId, isActive);
        }

        return buildSummary(employees, month, year);
    }

    // shared logic: given a list of employees, merge with their tour program data for month/year
    private List<TourProgramSummaryDTO> buildSummary(
            List<Employee> employees,
            Integer month,
            Integer year) {

        if (employees.isEmpty()) {
            return List.of();
        }

        List<Long> employeeIds = employees.stream()
                .map(Employee::getId)
                .toList();

        // fetch all tour programs for these employees in one query
        List<TourProgram> programs = tourProgramRepository
                .findByEmployeeIdsAndMonthAndYear(employeeIds, month, year);

        // map employee id to their tour program for fast lookup
        Map<Long, TourProgram> programByEmployeeId = programs.stream()
                .collect(Collectors.toMap(
                        tp -> tp.getEmployee().getId(),
                        tp -> tp
                ));

        List<TourProgramSummaryDTO> result = new ArrayList<>();

        for (Employee emp : employees) {
            TourProgram tp = programByEmployeeId.get(emp.getId());

            TourProgramSummaryDTO dto = new TourProgramSummaryDTO();
            dto.setEmployeeId(emp.getId());
            dto.setEmployeeName(emp.getName());
            dto.setEmployeeCode(emp.getUserCode());
            dto.setDesignation(emp.getDesignation() != null ? emp.getDesignation().getName() : null);
            dto.setStateName(emp.getState() != null ? emp.getState().getStateName() : null);
            dto.setHeadquarterName(emp.getDistrict() != null ? emp.getDistrict().getDistrictName() : null);

            if (tp != null) {
                dto.setTourProgramId(tp.getId());
                dto.setIsSubmitted(tp.getIsSubmitted());
                dto.setSubmittedAt(tp.getSubmittedAt());
                dto.setIsApproved(tp.getIsApproved());
                dto.setApprovedAt(tp.getApprovedAt());
                dto.setRejectionMessage(tp.getRejectionMessage());
            } else {
                // employee exists but has not submitted a plan for this month/year
                dto.setTourProgramId(null);
                dto.setIsSubmitted(false);
                dto.setIsApproved(false);
            }

            result.add(dto);
        }

        return result;
    }

    // returns the day-by-day detail for a tour program.
    // called when admin clicks "Yes" on the submitted column.
    public List<TourProgramDayDetailDTO> getTourProgramDetail(Long tourProgramId) {
        // verify the tour program exists
        tourProgramRepository.findById(tourProgramId)
                .orElseThrow(() -> new RuntimeException("Tour program not found with id: " + tourProgramId));

        List<TourProgramDay> days = tourProgramDayRepository
                .findByTourProgramIdOrderByDate(tourProgramId);

        return days.stream()
                .map(this::toDayDetailDTO)
                .toList();
    }

    // approve the entire monthly tour program for an employee
    @Transactional
    public void approveTourProgram(Long tourProgramId) {
        TourProgram tp = tourProgramRepository.findById(tourProgramId)
                .orElseThrow(() -> new RuntimeException("Tour program not found with id: " + tourProgramId));

        if (!Boolean.TRUE.equals(tp.getIsSubmitted())) {
            throw new RuntimeException("Cannot approve a plan that has not been submitted yet.");
        }

        tp.setIsApproved(true);
        tp.setApprovedAt(LocalDateTime.now());
        tp.setRejectionMessage(null);
        tourProgramRepository.save(tp);
    }

    // reject the tour program and store the reason so the MR can see why it was rejected
    @Transactional
    public void rejectTourProgram(Long tourProgramId, String rejectionMessage) {
        TourProgram tp = tourProgramRepository.findById(tourProgramId)
                .orElseThrow(() -> new RuntimeException("Tour program not found with id: " + tourProgramId));

        if (!Boolean.TRUE.equals(tp.getIsSubmitted())) {
            throw new RuntimeException("Cannot reject a plan that has not been submitted yet.");
        }

        tp.setIsApproved(false);
        tp.setRejectionMessage(rejectionMessage);
        tourProgramRepository.save(tp);
    }

    // maps a TourProgramDay entity to the DTO used by the calendar detail view.
    // builds the "From : {area} -- To : {area}" strings from the linked STP routes.
    private TourProgramDayDetailDTO toDayDetailDTO(TourProgramDay day) {
        TourProgramDayDetailDTO dto = new TourProgramDayDetailDTO();
        dto.setDate(day.getDate());
        dto.setActivityType(day.getActivityType());
        dto.setJointWorkWithPlan(day.getJointWorkWithPlan());
        dto.setRemark(day.getRemark());

        // build the area strings from linked STP routes
        List<String> areas = new ArrayList<>();
        if (day.getAreas() != null) {
            for (TourProgramDayArea dayArea : day.getAreas()) {
                if (dayArea.getStp() != null) {
                    String from = dayArea.getStp().getFromArea() != null
                            ? dayArea.getStp().getFromArea().getAreaName() : "---";
                    String to = dayArea.getStp().getToArea() != null
                            ? dayArea.getStp().getToArea().getAreaName() : "---";
                    areas.add("From : " + from + " -- To : " + to);
                }
            }
        }
        dto.setApprovedAreas(areas);

        // collect doctor names
        List<String> doctors = new ArrayList<>();
        if (day.getDoctors() != null) {
            day.getDoctors().forEach(d -> {
                if (d.getDoctor() != null) {
                    doctors.add(d.getDoctor().getDoctorName());
                }
            });
        }
        dto.setApprovedDoctors(doctors);

        // collect chemist/provider names
        List<String> chemists = new ArrayList<>();
        if (day.getChemists() != null) {
            day.getChemists().forEach(c -> {
                if (c.getProvider() != null) {
                    chemists.add(c.getProvider().getProviderName());
                }
            });
        }
        dto.setApprovedChemists(chemists);

        return dto;
    }
}