package com.mrreporting.backend.controller;

import com.mrreporting.backend.entity.Designation;
import com.mrreporting.backend.service.DesignationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/masters/designations")
@CrossOrigin(origins = "http://localhost:5173") // Updated to match Neeraj's exact local port
public class DesignationController {

    @Autowired
    private DesignationService designationService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createDesignation(@RequestBody Designation designation) {
        // 1. Save the data to the database
        Designation savedDesignation = designationService.saveDesignation(designation);

        // 2. Build the JSON response
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", savedDesignation);

        // 3. Send it back with a 200 OK status
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllDesignations() {
        List<Designation> designations = designationService.getAllDesignations();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", designations);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateDesignation(
            @PathVariable Long id,
            @RequestBody Designation designation) {
        try {
            // Call the service method we just created
            Designation updatedDesignation = designationService.updateDesignation(id, designation);

            // Build the JSON response the frontend expects
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Designation updated successfully");
            response.put("data", updatedDesignation);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/hierarchy")
    public ResponseEntity<Map<String, Object>> getHierarchyDesignations() {
        try {
            List<Designation> designations = designationService.getDesignationsForHierarchy();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", designations);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch hierarchy designations: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}