package com.mrreporting.backend.controller;

import com.mrreporting.backend.dto.StpEmployeeSummaryDTO;
import com.mrreporting.backend.dto.StpGeographySummaryDTO;
import com.mrreporting.backend.dto.StpRouteDetailDTO;
import com.mrreporting.backend.service.StpApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/approvals/stp")
@CrossOrigin(origins = "http://localhost:5173")
public class StpApprovalController {

    @Autowired
    private StpApprovalService stpApprovalService;

    // LEVEL 1: Geography Summary
    @GetMapping("/geography-summary")
    public ResponseEntity<Map<String, Object>> getGeographySummary() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<StpGeographySummaryDTO> summary = stpApprovalService.getGeographySummary();

            response.put("success", true);
            response.put("data", summary);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch geography summary: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // LEVEL 2: Employee Summary
    @GetMapping("/employee-summary")
    public ResponseEntity<Map<String, Object>> getEmployeeSummary(
            @RequestParam Integer districtId) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<StpEmployeeSummaryDTO> summary =
                    stpApprovalService.getEmployeeSummary(districtId);

            response.put("success", true);
            response.put("data", summary);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch employee summary: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // LEVEL 3: STP Route Details
    @GetMapping("/details")
    public ResponseEntity<Map<String, Object>> getStpDetails(
            @RequestParam Long employeeId,
            @RequestParam(required = false, defaultValue = "PENDING") String requestStatus) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<StpRouteDetailDTO> details =
                    stpApprovalService.getStpDetails(employeeId, requestStatus);

            response.put("success", true);
            response.put("data", details);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch STP details: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // LEVEL 4a: Approve selected STPs
    @PostMapping("/approve")
    public ResponseEntity<Map<String, Object>> approveStps(
            @RequestBody List<Long> stpIds) {

        Map<String, Object> response = new HashMap<>();
        try {
            int count = stpApprovalService.approveStps(stpIds);

            response.put("success", true);
            response.put("message", count + " STP route(s) approved successfully.");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to approve STPs: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // LEVEL 4b: Delete selected STPs
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteStps(
            @RequestBody List<Long> stpIds) {

        Map<String, Object> response = new HashMap<>();
        try {
            int count = stpApprovalService.deleteStps(stpIds);

            response.put("success", true);
            response.put("message", count + " STP route(s) deleted successfully.");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to delete STPs: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}