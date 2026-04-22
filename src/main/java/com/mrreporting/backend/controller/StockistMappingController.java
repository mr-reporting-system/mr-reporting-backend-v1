package com.mrreporting.backend.controller;

import com.mrreporting.backend.dto.StockistMappingEmployeeRowDTO;
import com.mrreporting.backend.dto.StockistMappingRequestDTO;
import com.mrreporting.backend.dto.StockistMappingResponseDTO;
import com.mrreporting.backend.dto.StockistMappingStockistRowDTO;
import com.mrreporting.backend.service.StockistMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/masters/stockist-mappings")
@CrossOrigin(origins = "http://localhost:5173")
public class StockistMappingController {

    @Autowired
    private StockistMappingService stockistMappingService;

    @GetMapping("/stockists")
    public ResponseEntity<Map<String, Object>> getStockistsForMapping(
            @RequestParam(required = false) Integer stateId,
            @RequestParam(required = false) List<Integer> districtIds,
            @RequestParam(required = false) Integer districtId) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<Integer> resolvedDistrictIds = resolveDistrictIds(districtIds, districtId);

            List<StockistMappingStockistRowDTO> stockists =
                    stockistMappingService.getStockistsForMapping(stateId, resolvedDistrictIds);

            response.put("success", true);
            response.put("data", stockists);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch stockists: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/employees")
    public ResponseEntity<Map<String, Object>> getEmployeesForMapping(
            @RequestParam(required = false) Integer stateId,
            @RequestParam(required = false) List<Integer> districtIds,
            @RequestParam(required = false) Integer districtId,
            @RequestParam(required = false) Long stockistId) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<Integer> resolvedDistrictIds = resolveDistrictIds(districtIds, districtId);

            List<StockistMappingEmployeeRowDTO> employees =
                    stockistMappingService.getEmployeesForMapping(stateId, resolvedDistrictIds, stockistId);

            response.put("success", true);
            response.put("data", employees);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch employees: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/map")
    public ResponseEntity<Map<String, Object>> mapStockistToEmployees(
            @RequestBody StockistMappingRequestDTO dto) {

        Map<String, Object> response = new HashMap<>();
        try {
            StockistMappingResponseDTO saved =
                    stockistMappingService.mapStockistToEmployees(dto);

            response.put("success", true);
            response.put("message", "Stockist mapping saved successfully.");
            response.put("data", saved);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to save stockist mapping: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    private List<Integer> resolveDistrictIds(List<Integer> districtIds, Integer districtId) {
        if (districtIds != null && !districtIds.isEmpty()) {
            return districtIds;
        }
        if (districtId != null) {
            return List.of(districtId);
        }
        return List.of();
    }
}
