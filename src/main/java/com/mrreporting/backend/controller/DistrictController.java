package com.mrreporting.backend.controller;

import com.mrreporting.backend.entity.District;
import com.mrreporting.backend.service.DistrictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/masters/districts")
@CrossOrigin(origins = "http://localhost:5173")
public class DistrictController {

    @Autowired
    private DistrictService districtService;

    // --- Filtered Endpoint (For Area/Doctor/Chemist Creation) ---
    @GetMapping
    public ResponseEntity<Map<String, Object>> getActiveDistrictsByState(@RequestParam("stateId") Integer stateId) {
        List<District> districts = districtService.getActiveDistrictsByState(stateId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", districts);

        return ResponseEntity.ok(response);
    }

    // --- Global Endpoint 👈 (For Employee Creation) ---
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllDistrictsByState(@RequestParam("stateId") Integer stateId) {
        List<District> districts = districtService.getAllDistrictsByState(stateId); // Calls the all-inclusive service method

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", districts);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/status")
    public ResponseEntity<Map<String, Object>> updateDistrictStatus(@RequestBody Map<String, Object> payload) {
        try {
            List<Integer> districtIds = (List<Integer>) payload.get("districtIds");
            boolean isActive = (boolean) payload.get("isActive");

            districtService.updateDistrictStatus(districtIds, isActive);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "District statuses updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to update districts: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createDistrict(@RequestBody Map<String, Object> payload) {
        try {
            Integer stateId = (Integer) payload.get("stateId");
            String districtName = (String) payload.get("districtName");

            District savedDistrict = districtService.createDistrict(stateId, districtName);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", savedDistrict);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to create district: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteDistrict(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        try {
            districtService.deleteDistrict(id);
            response.put("success", true);
            response.put("message", "District deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to delete district: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}