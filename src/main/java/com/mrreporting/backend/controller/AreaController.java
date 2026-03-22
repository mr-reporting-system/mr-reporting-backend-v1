package com.mrreporting.backend.controller;

import com.mrreporting.backend.dto.AreaDTO;
import com.mrreporting.backend.dto.AreaTransferDTO;
import com.mrreporting.backend.entity.Area;
import com.mrreporting.backend.service.AreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/masters/areas")
@CrossOrigin(origins = "http://localhost:5173")
public class AreaController {

    @Autowired
    private AreaService areaService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createArea(@RequestBody AreaDTO areaDTO) {
        try {
            Area savedArea = areaService.saveArea(areaDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Area addition request submitted for approval.");
            response.put("data", savedArea);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllAreas() {
        try {
            // only returns areas where is_active = true
            List<Area> areas = areaService.getAllActiveAreas();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", areas);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch areas: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateArea(@PathVariable Long id, @RequestBody AreaDTO dto) {
        Map<String, Object> response = new HashMap<>();
        try {
            Area updatedArea = areaService.updateArea(id, dto);
            response.put("success", true);
            response.put("data", updatedArea);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error updating area: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteArea(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            // This now moves the area to "Deletion Requests"
            areaService.requestAreaDeletion(id);
            response.put("success", true);
            response.put("message", "Deletion request submitted for approval.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error requesting deletion: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<Map<String, Object>> getFilteredAreas(
            @RequestParam(required = false) Integer stateId,
            @RequestParam(required = false) Integer districtId,
            @RequestParam(required = false) Long employeeId) {
        try {
            // Service/Repository logic now filters for isActive = true
            List<Area> areas = areaService.getFilteredAreas(stateId, districtId, employeeId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", areas);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to filter areas: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PutMapping("/transfer")
    public ResponseEntity<Map<String, Object>> transferAreas(@RequestBody AreaTransferDTO dto) {
        Map<String, Object> response = new HashMap<>();
        try {
            areaService.transferAreas(dto);
            response.put("success", true);
            response.put("message", "Areas transferred successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to transfer areas: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<Map<String, Object>> getAreasByEmployee(@PathVariable Long employeeId) {
        try {
            // Returns only active areas for the employee
            List<Area> areas = areaService.getAreasForEmployee(employeeId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", areas);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch areas: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}