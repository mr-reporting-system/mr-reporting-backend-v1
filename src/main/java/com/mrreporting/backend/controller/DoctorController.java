package com.mrreporting.backend.controller;

import com.mrreporting.backend.dto.DoctorDTO;
import com.mrreporting.backend.dto.ProviderTransferDTO;
import com.mrreporting.backend.entity.Doctor;
import com.mrreporting.backend.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
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
            Doctor savedDoctor = doctorService.saveDoctor(doctorDTO);

            response.put("success", true);
            response.put("message", "Doctor addition request submitted for approval.");
            response.put("data", savedDoctor);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error saving doctor: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllDoctors() {
        try {
            // only returns doctors where is_active = true
            List<Doctor> doctors = doctorService.getAllActiveDoctors();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", doctors);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch doctors");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/area/{areaId}")
    public ResponseEntity<Map<String, Object>> getDoctorsByArea(@PathVariable Long areaId) {
        try {
            // Returns only active doctors for this area
            List<Doctor> doctors = doctorService.getDoctorsByArea(areaId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", doctors);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch doctors for this area");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteDoctor(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Flag for deletion approval
            doctorService.requestDoctorDeletion(id);
            response.put("success", true);
            response.put("message", "Deletion request submitted for approval.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error requesting deletion: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/transfer")
    public ResponseEntity<Map<String, Object>> transferDoctors(@RequestBody ProviderTransferDTO dto) {
        Map<String, Object> response = new HashMap<>();
        try {
            doctorService.transferDoctors(dto);
            response.put("success", true);
            response.put("message", "Doctors transferred successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to transfer doctors: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}