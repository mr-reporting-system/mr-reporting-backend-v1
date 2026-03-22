package com.mrreporting.backend.controller;

import com.mrreporting.backend.dto.StpRequestDTO;
import com.mrreporting.backend.entity.Stp;
import com.mrreporting.backend.service.StpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/masters/stps")
public class StpController {

    @Autowired
    private StpService stpService;

    // 🌟 Uses POST to securely receive the JSON body and create a new record
    @PostMapping
    public ResponseEntity<Map<String, Object>> createStp(@RequestBody StpRequestDTO dto) {
        try {
            Stp newStp = stpService.createStp(dto);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "STP created successfully");
            response.put("data", newStp);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to create STP: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<Map<String, Object>> getStpsByEmployee(@PathVariable Long employeeId) {
        try {
            List<Stp> stps = stpService.getStpsByEmployeeId(employeeId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", stps);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch STPs: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteStps(@RequestBody Map<String, List<Long>> payload) {
        try {
            List<Long> stpIds = payload.get("stpIds");
            stpService.deleteStps(stpIds);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "STPs deleted successfully.");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to delete STPs: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateStp(@PathVariable Long id, @RequestBody StpRequestDTO dto) {
        try {
            Stp updatedStp = stpService.updateStp(id, dto);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "STP updated successfully");
            response.put("data", updatedStp);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to update STP: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}