package com.mrreporting.backend.controller;

import com.mrreporting.backend.dto.MrTourProgramMonthDetailDTO;
import com.mrreporting.backend.dto.MrTourProgramSubmitRequestDTO;
import com.mrreporting.backend.dto.MrTourProgramSubmitResponseDTO;
import com.mrreporting.backend.service.MrTourProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/mr/tour-program")
@CrossOrigin(origins = "http://localhost:5173")
public class MrTourProgramController {

    @Autowired
    private MrTourProgramService mrTourProgramService;

    @PostMapping("/submit")
    public ResponseEntity<Map<String, Object>> submitTourProgram(@RequestBody MrTourProgramSubmitRequestDTO dto) {
        Map<String, Object> response = new HashMap<>();
        try {
            MrTourProgramSubmitResponseDTO saved = mrTourProgramService.submitTourProgram(dto);
            response.put("success", true);
            response.put("message", "Tour program submitted successfully.");
            response.put("data", saved);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to submit tour program: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/month-detail")
    public ResponseEntity<Map<String, Object>> getMonthDetail(
            @RequestParam Integer month,
            @RequestParam Integer year) {

        Map<String, Object> response = new HashMap<>();
        try {
            MrTourProgramMonthDetailDTO detail = mrTourProgramService.getMonthDetail(month, year);
            response.put("success", true);
            response.put("data", detail);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch tour program detail: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
