package com.mrreporting.backend.controller;

import com.mrreporting.backend.dto.DeleteStockistMappingsRequestDTO;
import com.mrreporting.backend.dto.DeleteStockistMappingsResponseDTO;
import com.mrreporting.backend.dto.StockistMappingReportFilterDTO;
import com.mrreporting.backend.dto.StockistMappingReportResponseDTO;
import com.mrreporting.backend.service.StockistMappingReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/masters/stockist-mapping-report")
@CrossOrigin(origins = "http://localhost:5173")
public class StockistMappingReportController {

    @Autowired
    private StockistMappingReportService stockistMappingReportService;

    @PostMapping("/view")
    public ResponseEntity<Map<String, Object>> getMappedReport(
            @RequestBody StockistMappingReportFilterDTO dto) {

        Map<String, Object> response = new HashMap<>();
        try {
            StockistMappingReportResponseDTO report =
                    stockistMappingReportService.getMappedReport(dto);

            response.put("success", true);
            response.put("data", report);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch mapped stockist report: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteMappings(
            @RequestBody DeleteStockistMappingsRequestDTO dto) {

        Map<String, Object> response = new HashMap<>();
        try {
            DeleteStockistMappingsResponseDTO deleted =
                    stockistMappingReportService.deleteMappings(dto);

            response.put("success", true);
            response.put("message", "Selected stockist mappings deleted successfully.");
            response.put("data", deleted);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to delete stockist mappings: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
