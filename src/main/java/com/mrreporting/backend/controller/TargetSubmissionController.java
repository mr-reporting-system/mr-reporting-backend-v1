package com.mrreporting.backend.controller;

import com.mrreporting.backend.dto.*;
import com.mrreporting.backend.service.TargetSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/target/submission")
@CrossOrigin(origins = "http://localhost:5173")
public class TargetSubmissionController {

    @Autowired
    private TargetSubmissionService targetSubmissionService;

    @GetMapping("/employees")
    public ResponseEntity<Map<String, Object>> getActiveMrEmployees(
            @RequestParam Integer stateId,
            @RequestParam Integer districtId) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<DropdownOptionDTO> employees =
                    targetSubmissionService.getActiveMrEmployees(stateId, districtId);

            response.put("success", true);
            response.put("data", employees);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch target employees: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> submitTargets(
            @RequestBody TargetSubmissionRequestDTO dto) {

        Map<String, Object> response = new HashMap<>();
        try {
            TargetSubmissionResponseDTO saved =
                    targetSubmissionService.submitTargets(dto);

            response.put("success", true);
            response.put("message", "Targets submitted successfully.");
            response.put("data", saved);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to submit targets: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/modify/form")
    public ResponseEntity<Map<String, Object>> getTargetModifyForm(
            @RequestParam Long employeeId,
            @RequestParam Integer year) {

        Map<String, Object> response = new HashMap<>();
        try {
            TargetModifyFormDTO form =
                    targetSubmissionService.getTargetModifyForm(employeeId, year);

            response.put("success", true);
            response.put("data", form);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch target modify form: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/modify")
    public ResponseEntity<Map<String, Object>> modifyTargets(
            @RequestBody TargetModifyRequestDTO dto) {

        Map<String, Object> response = new HashMap<>();
        try {
            TargetSubmissionResponseDTO saved =
                    targetSubmissionService.modifyTargets(dto);

            response.put("success", true);
            response.put("message", "Targets modified successfully.");
            response.put("data", saved);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to modify targets: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/employees/by-filters")
    public ResponseEntity<Map<String, Object>> getActiveMrEmployeesForFilters(
            @RequestParam List<Integer> stateIds,
            @RequestParam List<Integer> districtIds) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<DropdownOptionDTO> employees =
                    targetSubmissionService.getActiveMrEmployeesForFilters(stateIds, districtIds);

            response.put("success", true);
            response.put("data", employees);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch target employees: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/report")
    public ResponseEntity<Map<String, Object>> getTargetReport(
            @RequestBody TargetReportRequestDTO dto) {

        Map<String, Object> response = new HashMap<>();
        try {
            TargetReportResponseDTO report =
                    targetSubmissionService.getTargetReport(dto);

            response.put("success", true);
            response.put("data", report);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch target report: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


}
