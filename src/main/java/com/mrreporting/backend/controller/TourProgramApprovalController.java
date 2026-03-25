package com.mrreporting.backend.controller;

import com.mrreporting.backend.dto.TourProgramDayDetailDTO;
import com.mrreporting.backend.dto.TourProgramSummaryDTO;
import com.mrreporting.backend.entity.Employee;
import com.mrreporting.backend.service.EmployeeService;
import com.mrreporting.backend.service.TourProgramApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/approvals/tour-program")
@CrossOrigin(origins = "http://localhost:5173")
public class TourProgramApprovalController {

    @Autowired
    private TourProgramApprovalService tourProgramApprovalService;

    @Autowired
    private EmployeeService employeeService;

    // dropdown: employees filtered by designation and active status.
    // used in the hierarchical filter's "Select Employee" multi-select.
    // GET /api/approvals/tour-program/employees?designationId=3&isActive=true
    @GetMapping("/employees")
    public ResponseEntity<Map<String, Object>> getEmployeesByDesignationAndStatus(
            @RequestParam Long designationId,
            @RequestParam(required = false, defaultValue = "true") Boolean isActive) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<Employee> employees =
                    employeeService.getEmployeesByDesignationAndStatus(designationId, isActive);

            response.put("success", true);
            response.put("data", employees);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch employees: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // geographical summary: finds all employees in the selected districts
    // and returns their tour program submission/approval status for the given month and year.
    // GET /api/approvals/tour-program/geographical?districtIds=1,2,3&month=2&year=2026
    @GetMapping("/geographical")
    public ResponseEntity<Map<String, Object>> getGeographicalSummary(
            @RequestParam List<Integer> districtIds,
            @RequestParam Integer month,
            @RequestParam Integer year) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<TourProgramSummaryDTO> summary =
                    tourProgramApprovalService.getGeographicalSummary(districtIds, month, year);

            response.put("success", true);
            response.put("data", summary);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch geographical summary: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // hierarchical summary: finds employees by designation and status,
    // optionally filtered down to specific employee IDs.
    // GET /api/approvals/tour-program/hierarchical?designationId=3&isActive=true&employeeIds=1,2&month=2&year=2026
    @GetMapping("/hierarchical")
    public ResponseEntity<Map<String, Object>> getHierarchicalSummary(
            @RequestParam Long designationId,
            @RequestParam(required = false, defaultValue = "true") Boolean isActive,
            @RequestParam(required = false) List<Long> employeeIds,
            @RequestParam Integer month,
            @RequestParam Integer year) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<TourProgramSummaryDTO> summary =
                    tourProgramApprovalService.getHierarchicalSummary(
                            designationId, isActive, employeeIds, month, year);

            response.put("success", true);
            response.put("data", summary);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch hierarchical summary: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // detail view: returns the full day-by-day calendar for a submitted tour program.
    // called when admin clicks "Yes" on the submitted column in the summary table.
    // GET /api/approvals/tour-program/detail/{tourProgramId}
    @GetMapping("/detail/{tourProgramId}")
    public ResponseEntity<Map<String, Object>> getTourProgramDetail(
            @PathVariable Long tourProgramId) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<TourProgramDayDetailDTO> detail =
                    tourProgramApprovalService.getTourProgramDetail(tourProgramId);

            response.put("success", true);
            response.put("data", detail);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch tour program detail: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // approve a submitted monthly tour program.
    // PUT /api/approvals/tour-program/approve/{tourProgramId}
    @PutMapping("/approve/{tourProgramId}")
    public ResponseEntity<Map<String, Object>> approveTourProgram(
            @PathVariable Long tourProgramId) {

        Map<String, Object> response = new HashMap<>();
        try {
            tourProgramApprovalService.approveTourProgram(tourProgramId);

            response.put("success", true);
            response.put("message", "Tour program approved successfully.");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to approve tour program: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // reject a submitted monthly tour program with a reason message.
    // PUT /api/approvals/tour-program/reject/{tourProgramId}
    // body: { "rejectionMessage": "Plan is incomplete for week 3" }
    @PutMapping("/reject/{tourProgramId}")
    public ResponseEntity<Map<String, Object>> rejectTourProgram(
            @PathVariable Long tourProgramId,
            @RequestBody Map<String, String> body) {

        Map<String, Object> response = new HashMap<>();
        try {
            String rejectionMessage = body.get("rejectionMessage");
            if (rejectionMessage == null || rejectionMessage.isBlank()) {
                response.put("success", false);
                response.put("message", "Rejection message is required.");
                return ResponseEntity.badRequest().body(response);
            }

            tourProgramApprovalService.rejectTourProgram(tourProgramId, rejectionMessage);

            response.put("success", true);
            response.put("message", "Tour program rejected.");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to reject tour program: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}