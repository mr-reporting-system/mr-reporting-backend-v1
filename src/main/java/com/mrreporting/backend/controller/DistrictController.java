package com.mrreporting.backend.controller;

import com.mrreporting.backend.entity.District;
import com.mrreporting.backend.repository.DistrictRepository;
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
    private DistrictRepository districtRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDistrictsByState(@RequestParam("stateId") Integer stateId) {
        List<District> districts = districtRepository.findByStateId(stateId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", districts);

        return ResponseEntity.ok(response);
    }
}