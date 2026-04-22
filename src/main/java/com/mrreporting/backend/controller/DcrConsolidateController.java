package com.mrreporting.backend.controller;

import com.mrreporting.backend.dto.DcrConsolidateFilterDTO;
import com.mrreporting.backend.dto.DcrConsolidateResponseDTO;
import com.mrreporting.backend.dto.DcrDateWiseResponseDTO;
import com.mrreporting.backend.service.DcrConsolidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/report-analysis/dcr-consolidate")
@CrossOrigin(origins = "http://localhost:5173")
public class DcrConsolidateController {

    @Autowired
    private DcrConsolidateService dcrConsolidateService;

    @PostMapping("/summary")
    public ResponseEntity<Map<String, Object>> getConsolidateSummary(
            @RequestBody DcrConsolidateFilterDTO dto) {

        Map<String, Object> response = new HashMap<>();
        try {
            DcrConsolidateResponseDTO report =
                    dcrConsolidateService.getConsolidateSummary(dto);

            response.put("success", true);
            response.put("data", report);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch DCR consolidate summary: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/detail/{employeeId}")
    public ResponseEntity<Map<String, Object>> getEmployeeDateWiseReport(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        Map<String, Object> response = new HashMap<>();
        try {
            DcrDateWiseResponseDTO detail =
                    dcrConsolidateService.getEmployeeDateWiseReport(employeeId, fromDate, toDate);

            response.put("success", true);
            response.put("data", detail);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch DCR date-wise report: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
