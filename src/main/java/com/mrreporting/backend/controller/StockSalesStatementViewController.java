package com.mrreporting.backend.controller;

import com.mrreporting.backend.dto.DropdownOptionDTO;
import com.mrreporting.backend.dto.SSSViewFilterDTO;
import com.mrreporting.backend.dto.SSSViewReportResponseDTO;
import com.mrreporting.backend.service.StockSalesStatementViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expense/sss-view")
@CrossOrigin(origins = "http://localhost:5173")
public class StockSalesStatementViewController {

    @Autowired
    private StockSalesStatementViewService stockSalesStatementViewService;

    @GetMapping("/states")
    public ResponseEntity<Map<String, Object>> getStates() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<DropdownOptionDTO> states = stockSalesStatementViewService.getActiveStates();
            response.put("success", true);
            response.put("data", states);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch states: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/districts")
    public ResponseEntity<Map<String, Object>> getDistricts(
            @RequestParam(required = false) List<Integer> stateIds) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<DropdownOptionDTO> districts =
                    stockSalesStatementViewService.getDistrictsByStates(stateIds);

            response.put("success", true);
            response.put("data", districts);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch districts: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/employees")
    public ResponseEntity<Map<String, Object>> getEmployees(
            @RequestParam(required = false) List<Integer> districtIds) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<DropdownOptionDTO> employees =
                    stockSalesStatementViewService.getEmployeesByDistricts(districtIds);

            response.put("success", true);
            response.put("data", employees);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch employees: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/providers")
    public ResponseEntity<Map<String, Object>> getProviders(
            @RequestParam(required = false) List<Long> employeeIds) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<DropdownOptionDTO> providers =
                    stockSalesStatementViewService.getProvidersByEmployees(employeeIds);

            response.put("success", true);
            response.put("data", providers);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch providers: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/report")
    public ResponseEntity<Map<String, Object>> getReport(@RequestBody SSSViewFilterDTO dto) {
        Map<String, Object> response = new HashMap<>();
        try {
            SSSViewReportResponseDTO report = stockSalesStatementViewService.getReport(dto);

            response.put("success", true);
            response.put("data", report);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch SSS view report: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
