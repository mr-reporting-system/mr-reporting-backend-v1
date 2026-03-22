package com.mrreporting.backend.controller;

import com.mrreporting.backend.dto.ApprovalSummaryDTO;
import com.mrreporting.backend.service.ApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/approvals")
@CrossOrigin(origins = "http://localhost:5173")
public class ApprovalController {

    @Autowired
    private ApprovalService approvalService;

    @GetMapping("/counts")
    public ResponseEntity<Map<String, Object>> getDashboardCounts() {
        try {
            Map<String, Object> counts = approvalService.getDashboardCounts();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", counts);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch approval counts: " + e.getMessage());

            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/summary/{category}/{type}")
    public ResponseEntity<Map<String, Object>> getSummary(
            @PathVariable String category,
            @PathVariable String type) {
        try {
            List<ApprovalSummaryDTO> summary = approvalService.getCategorySummary(category, type);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", summary);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/details/{category}/{type}/{employeeId}")
    public ResponseEntity<Map<String, Object>> getPendingDetails(
            @PathVariable String category,
            @PathVariable String type,
            @PathVariable Long employeeId) {
        try {
            // This calls the switch logic we planned for the Service
            List<?> details = approvalService.getPendingDetails(category, type, employeeId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", details);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch details: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/approve/{category}/{type}")
    public ResponseEntity<Map<String, Object>> approveRecords(
            @PathVariable String category,
            @PathVariable String type,
            @RequestBody List<Long> ids) {
        try {
            approvalService.approveRecords(category, type, ids);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Selected items approved successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Approval failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/reject/{category}/{type}")
    public ResponseEntity<Map<String, Object>> rejectRecords(
            @PathVariable String category,
            @PathVariable String type,
            @RequestBody List<Long> ids) {
        try {
            approvalService.rejectRecords(category, type, ids);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Selected items rejected.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Rejection failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}