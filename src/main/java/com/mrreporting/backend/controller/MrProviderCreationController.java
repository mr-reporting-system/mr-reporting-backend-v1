package com.mrreporting.backend.controller;

import com.mrreporting.backend.dto.DoctorDTO;
import com.mrreporting.backend.dto.DropdownOptionDTO;
import com.mrreporting.backend.dto.ProviderDTO;
import com.mrreporting.backend.entity.Doctor;
import com.mrreporting.backend.entity.Provider;
import com.mrreporting.backend.service.MrProviderCreationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mr/provider-creation")
@CrossOrigin(origins = "http://localhost:5173")
public class MrProviderCreationController {

    @Autowired
    private MrProviderCreationService mrProviderCreationService;

    @GetMapping("/areas")
    public ResponseEntity<Map<String, Object>> getAreasForCurrentMr() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<DropdownOptionDTO> areas = mrProviderCreationService.getAreasForCurrentMr();
            response.put("success", true);
            response.put("data", areas);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch MR areas: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> getProducts() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<DropdownOptionDTO> products = mrProviderCreationService.getProducts();
            response.put("success", true);
            response.put("data", products);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch products: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/doctors")
    public ResponseEntity<Map<String, Object>> createDoctor(@RequestBody DoctorDTO doctorDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            Doctor savedDoctor = mrProviderCreationService.createDoctorForCurrentMr(doctorDTO);
            response.put("success", true);
            response.put("message", "Doctor addition request submitted for approval.");
            response.put("data", savedDoctor);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error saving MR doctor: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/providers")
    public ResponseEntity<Map<String, Object>> createProvider(@RequestBody ProviderDTO providerDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            Provider savedProvider = mrProviderCreationService.createProviderForCurrentMr(providerDTO);
            response.put("success", true);
            response.put("message", "Provider addition request submitted for approval.");
            response.put("data", savedProvider);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error saving MR provider: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
