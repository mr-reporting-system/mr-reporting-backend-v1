package com.mrreporting.backend.controller;

import com.mrreporting.backend.dto.DoctorDTO;
import com.mrreporting.backend.entity.Doctor;
import com.mrreporting.backend.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/masters/doctors")
@CrossOrigin(origins = "http://localhost:5173")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createDoctor(@RequestBody DoctorDTO doctorDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Call our service to process the nested DTO and save to 4 tables
            Doctor savedDoctor = doctorService.saveDoctor(doctorDTO);

            response.put("success", true);
            response.put("data", savedDoctor);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error saving doctor: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}