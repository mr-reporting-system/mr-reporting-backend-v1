package com.mrreporting.backend.controller;

import com.mrreporting.backend.dto.ProviderDTO;
import com.mrreporting.backend.entity.Provider;
import com.mrreporting.backend.service.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/masters/providers")
@CrossOrigin(origins = "http://localhost:5173")
public class ProviderController {

    @Autowired
    private ProviderService providerService;

    // 1. Create Chemist / Stockist 💾
    @PostMapping
    public ResponseEntity<Map<String, Object>> createProvider(@RequestBody ProviderDTO providerDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            Provider savedProvider = providerService.saveProvider(providerDTO);
            response.put("success", true);
            response.put("data", savedProvider);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error saving provider: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 2. Get All Providers 📋
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllProviders() {
        try {
            List<Provider> providers = providerService.getAllProviders();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", providers);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch providers");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}