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

    // returns active districts for a single state, used in most master creation forms
    @GetMapping
    public ResponseEntity<Map<String, Object>> getActiveDistrictsByState(
            @RequestParam("stateId") Integer stateId) {

        List<District> districts = districtService.getActiveDistrictsByState(stateId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", districts);

        return ResponseEntity.ok(response);
    }

    // returns all districts for a single state, used in employee creation form
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllDistrictsByState(
            @RequestParam("stateId") Integer stateId) {

        List<District> districts = districtService.getAllDistrictsByState(stateId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", districts);

        return ResponseEntity.ok(response);
    }

    // returns active districts for multiple states, used in CRM and tour program filters
    @GetMapping("/by-states")
    public ResponseEntity<Map<String, Object>> getActiveDistrictsByStates(
            @RequestParam List<Integer> stateIds) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<District> districts = districtService.getActiveDistrictsByStates(stateIds);
            response.put("success", true);
            response.put("data", districts);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch districts: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PutMapping("/status")
    public ResponseEntity<Map<String, Object>> updateDistrictStatus(
            @RequestBody Map<String, Object> payload) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<Integer> districtIds = (List<Integer>) payload.get("districtIds");
            boolean isActive = (boolean) payload.get("isActive");

            districtService.updateDistrictStatus(districtIds, isActive);

            response.put("success", true);
            response.put("message", "District statuses updated successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to update districts: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createDistrict(
            @RequestBody Map<String, Object> payload) {

        Map<String, Object> response = new HashMap<>();
        try {
            Integer stateId = (Integer) payload.get("stateId");
            String districtName = (String) payload.get("districtName");

            District savedDistrict = districtService.createDistrict(stateId, districtName);

            response.put("success", true);
            response.put("data", savedDistrict);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to create district: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
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