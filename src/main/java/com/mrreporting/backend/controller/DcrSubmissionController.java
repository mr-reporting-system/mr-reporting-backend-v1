package com.mrreporting.backend.controller;

import com.mrreporting.backend.dto.DcrSubmitRequestDTO;
import com.mrreporting.backend.dto.DcrSubmitResponseDTO;
import com.mrreporting.backend.dto.DropdownOptionDTO;
import com.mrreporting.backend.service.DcrSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class DcrSubmissionController {

    @Autowired
    private DcrSubmissionService dcrSubmissionService;

    @GetMapping("/employees/managers")
    public ResponseEntity<Map<String, Object>> getManagers() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<DropdownOptionDTO> managers = dcrSubmissionService.getManagers();
            response.put("success", true);
            response.put("data", managers);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch managers: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/areas")
    public ResponseEntity<Map<String, Object>> getAreasForCurrentMr() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<DropdownOptionDTO> areas = dcrSubmissionService.getAreasForCurrentMr();
            response.put("success", true);
            response.put("data", areas);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch areas: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/doctors/mapped")
    public ResponseEntity<Map<String, Object>> getMappedDoctors() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<DropdownOptionDTO> doctors = dcrSubmissionService.getMappedDoctorsForCurrentMr();
            response.put("success", true);
            response.put("data", doctors);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch mapped doctors: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> getProducts() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<DropdownOptionDTO> products = dcrSubmissionService.getProducts();
            response.put("success", true);
            response.put("data", products);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch products: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/chemists-stockists")
    public ResponseEntity<Map<String, Object>> getChemistsAndStockists() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<DropdownOptionDTO> providers = dcrSubmissionService.getChemistsAndStockistsForCurrentMr();
            response.put("success", true);
            response.put("data", providers);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch chemists/stockists: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/dcr")
    public ResponseEntity<Map<String, Object>> submitDcr(@RequestBody DcrSubmitRequestDTO dto) {
        Map<String, Object> response = new HashMap<>();
        try {
            DcrSubmitResponseDTO saved = dcrSubmissionService.submitDcr(dto);
            response.put("success", true);
            response.put("message", "DCR submitted successfully.");
            response.put("data", saved);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to submit DCR: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
