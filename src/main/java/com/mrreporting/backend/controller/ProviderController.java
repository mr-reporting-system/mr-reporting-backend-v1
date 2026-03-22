package com.mrreporting.backend.controller;

import com.mrreporting.backend.dto.ProviderDTO;
import com.mrreporting.backend.dto.ProviderTransferDTO;
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

    @PostMapping
    public ResponseEntity<Map<String, Object>> createProvider(@RequestBody ProviderDTO providerDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            Provider savedProvider = providerService.saveProvider(providerDTO);
            response.put("success", true);
            response.put("message", "Provider addition request submitted for approval.");
            response.put("data", savedProvider);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error saving provider: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllProviders() {
        try {
            // Only returns providers where is_active = true
            List<Provider> providers = providerService.getAllActiveProviders();
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

    @GetMapping("/area/{areaId}")
    public ResponseEntity<Map<String, Object>> getProvidersByArea(
            @PathVariable Long areaId,
            @RequestParam String type) {
        try {
            // Returns active providers for this area and type
            List<Provider> providers = providerService.getProvidersByAreaAndType(areaId, type);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", providers);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch " + type + "s for this area");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteProvider(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Flag for deletion approval
            providerService.requestProviderDeletion(id);
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
    public ResponseEntity<Map<String, Object>> transferProviders(@RequestBody ProviderTransferDTO dto) {
        Map<String, Object> response = new HashMap<>();
        try {
            providerService.transferProviders(dto);
            response.put("success", true);
            response.put("message", "Providers transferred successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to transfer providers: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}