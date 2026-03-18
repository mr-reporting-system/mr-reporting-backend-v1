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
    public ResponseEntity<Designation> createDesignation(@RequestBody Designation designation) {
        Designation savedDesignation = designationService.saveDesignation(designation);
        return ResponseEntity.ok(savedDesignation);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllDesignations() {
        List<Designation> designations = designationService.getAllDesignations();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", designations);

        return ResponseEntity.ok(response);
    }
}