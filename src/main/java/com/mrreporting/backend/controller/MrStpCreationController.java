package com.mrreporting.backend.controller;

import com.mrreporting.backend.dto.DropdownOptionDTO;
import com.mrreporting.backend.dto.MrStpCreateRequestDTO;
import com.mrreporting.backend.dto.MrStpCreateResponseDTO;
import com.mrreporting.backend.dto.MrStpRowDTO;
import com.mrreporting.backend.service.MrStpCreationService;
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
@RequestMapping("/api/mr/stp")
@CrossOrigin(origins = "http://localhost:5173")
public class MrStpCreationController {

    @Autowired
    private MrStpCreationService mrStpCreationService;

    @GetMapping("/areas")
    public ResponseEntity<Map<String, Object>> getAreasForCurrentMr() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<DropdownOptionDTO> areas = mrStpCreationService.getAreasForCurrentMr();
            response.put("success", true);
            response.put("data", areas);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch STP areas: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createStps(@RequestBody MrStpCreateRequestDTO dto) {
        Map<String, Object> response = new HashMap<>();
        try {
            MrStpCreateResponseDTO saved = mrStpCreationService.createStpsForCurrentMr(dto);
            response.put("success", true);
            response.put("message", "STP created successfully.");
            response.put("data", saved);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to create STP: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getStpsForCurrentMr() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<MrStpRowDTO> rows = mrStpCreationService.getStpsForCurrentMr();
            response.put("success", true);
            response.put("data", rows);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch STPs: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
