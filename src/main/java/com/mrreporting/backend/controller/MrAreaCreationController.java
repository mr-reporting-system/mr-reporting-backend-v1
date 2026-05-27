package com.mrreporting.backend.controller;

import com.mrreporting.backend.dto.MrAreaRequestDTO;
import com.mrreporting.backend.dto.MrAreaRowDTO;
import com.mrreporting.backend.service.MrAreaCreationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mr/area-creation")
@CrossOrigin(origins = "http://localhost:5173")
public class MrAreaCreationController {

    @Autowired
    private MrAreaCreationService mrAreaCreationService;

    @GetMapping("/areas")
    public ResponseEntity<Map<String, Object>> getAreasForCurrentMr() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<MrAreaRowDTO> areas = mrAreaCreationService.getAreasForCurrentMr();
            response.put("success", true);
            response.put("data", areas);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch MR areas: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/areas")
    public ResponseEntity<Map<String, Object>> createArea(@RequestBody MrAreaRequestDTO dto) {
        Map<String, Object> response = new HashMap<>();
        try {
            MrAreaRowDTO savedArea = mrAreaCreationService.createAreaForCurrentMr(dto);
            response.put("success", true);
            response.put("message", "Area addition request submitted for approval.");
            response.put("data", savedArea);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error creating MR area: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/areas/{id}")
    public ResponseEntity<Map<String, Object>> updateArea(@PathVariable Long id, @RequestBody MrAreaRequestDTO dto) {
        Map<String, Object> response = new HashMap<>();
        try {
            MrAreaRowDTO updatedArea = mrAreaCreationService.updateAreaForCurrentMr(id, dto);
            response.put("success", true);
            response.put("data", updatedArea);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error updating MR area: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/areas/{id}")
    public ResponseEntity<Map<String, Object>> deleteArea(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            mrAreaCreationService.requestAreaDeletionForCurrentMr(id);
            response.put("success", true);
            response.put("message", "Deletion request submitted for approval.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error requesting MR area deletion: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
