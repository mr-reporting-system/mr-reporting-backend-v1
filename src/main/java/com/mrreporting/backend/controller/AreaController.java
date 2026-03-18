package com.mrreporting.backend.controller;

import com.mrreporting.backend.dto.AreaDTO;
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
            // 1. Call the service to handle the logic and save
            Area savedArea = areaService.saveArea(areaDTO);

            // 2. Prepare the success response for the frontend
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", savedArea);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 3. Handle errors (like if a State or Employee ID doesn't exist)
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllAreas() {
        try {
            List<Area> areas = areaService.getAllAreas();

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
}