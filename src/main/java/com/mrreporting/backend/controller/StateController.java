package com.mrreporting.backend.controller;

import com.mrreporting.backend.entity.State;
import com.mrreporting.backend.service.StateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/masters/states")
@CrossOrigin(origins = "http://localhost:5173")
public class StateController {

    @Autowired
    private StateService stateService;

    // --- Filtered Endpoint (For Area/Doctor/Chemist Creation) ---
    @GetMapping
    public ResponseEntity<Map<String, Object>> getActiveStates() {
        List<State> states = stateService.getActiveStates();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", states);

        return ResponseEntity.ok(response);
    }

    // --- Global Endpoint 👈 (For Employee Creation) ---
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllStates() {
        List<State> states = stateService.getAllStates(); // Calls the all-inclusive service method

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", states);

        return ResponseEntity.ok(response);
    }
}