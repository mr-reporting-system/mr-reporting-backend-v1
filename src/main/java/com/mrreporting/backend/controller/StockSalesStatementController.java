package com.mrreporting.backend.controller;

import com.mrreporting.backend.dto.StockSalesStatementFormResponseDTO;
import com.mrreporting.backend.dto.StockSalesStatementRequestDTO;
import com.mrreporting.backend.dto.StockistOptionDTO;
import com.mrreporting.backend.service.StockSalesStatementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expense/sss")
@CrossOrigin(origins = "http://localhost:5173")
public class StockSalesStatementController {

    @Autowired
    private StockSalesStatementService stockSalesStatementService;

    @GetMapping({"/stockists", "/providers"})
    public ResponseEntity<Map<String, Object>> getProvidersByEmployee(
            @RequestParam Long employeeId,
            @RequestParam(required = false) Integer stateId,
            @RequestParam(required = false) Integer districtId) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<StockistOptionDTO> providers =
                    stockSalesStatementService.getStockistsByEmployee(employeeId, stateId, districtId);

            response.put("success", true);
            response.put("data", providers);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch providers: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/form")
    public ResponseEntity<Map<String, Object>> getStatementForm(
            @RequestParam(required = false) Long providerId,
            @RequestParam(required = false) Long stockistId,
            @RequestParam(required = false) Integer stateId,
            @RequestParam Integer month,
            @RequestParam Integer year) {

        Map<String, Object> response = new HashMap<>();
        try {
            Long resolvedProviderId = providerId != null ? providerId : stockistId;
            if (resolvedProviderId == null) {
                response.put("success", false);
                response.put("message", "Provider is required.");
                return ResponseEntity.badRequest().body(response);
            }

            StockSalesStatementFormResponseDTO form =
                    stockSalesStatementService.getStatementForm(resolvedProviderId, stateId, month, year);

            response.put("success", true);
            response.put("data", form);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch SSS form: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> saveStatement(
            @RequestBody StockSalesStatementRequestDTO dto) {

        Map<String, Object> response = new HashMap<>();
        try {
            StockSalesStatementFormResponseDTO saved =
                    stockSalesStatementService.saveStatement(dto);

            response.put("success", true);
            response.put("message", "Stock-Sales & Statement saved successfully.");
            response.put("data", saved);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to save SSS: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
