package com.mrreporting.backend.controller;

import com.mrreporting.backend.entity.State;
import com.mrreporting.backend.repository.StateRepository;
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
    private StateRepository stateRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllStates() {
        List<State> states = stateRepository.findAll();

        // Wrapped in Neeraj's requested format
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", states);

        return ResponseEntity.ok(response);
    }
}