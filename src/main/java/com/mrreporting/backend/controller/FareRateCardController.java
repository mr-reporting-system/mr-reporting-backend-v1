package com.mrreporting.backend.controller;

import com.mrreporting.backend.dto.FareRateCardCreateDTO;
import com.mrreporting.backend.dto.FareRateCardResponseDTO;
import com.mrreporting.backend.dto.FareRateCardUpdateDTO;
import com.mrreporting.backend.entity.Designation;
import com.mrreporting.backend.service.DesignationService;
import com.mrreporting.backend.service.FareRateCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expense/frc")
@CrossOrigin(origins = "http://localhost:5173")
public class FareRateCardController {

    @Autowired
    private FareRateCardService fareRateCardService;

    @Autowired
    private DesignationService designationService;

    // dropdown: all designations for the "Select Designation" multi-select at the top.
    // reuses the existing DesignationService — no new query needed.
    // GET /api/expense/frc/designations
    @GetMapping("/designations")
    public ResponseEntity<Map<String, Object>> getAllDesignations() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Designation> designations = designationService.getAllDesignations();
            response.put("success", true);
            response.put("data", designations);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch designations: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // create FRC records in bulk.
    // POST /api/expense/frc
    // body: { "designationIds": [1,2], "rows": [{ fromDistance, toDistance, ... }] }
    // saves designationIds.size() × rows.size() records in one call.
    @PostMapping
    public ResponseEntity<Map<String, Object>> createFareRateCards(
            @RequestBody FareRateCardCreateDTO dto) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<FareRateCardResponseDTO> saved = fareRateCardService.createFareRateCards(dto);
            response.put("success", true);
            response.put("message", saved.size() + " fare rate card(s) created successfully.");
            response.put("data", saved);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to create fare rate cards: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // fetch all FRC records for the table, optionally filtered by designationIds.
    // GET /api/expense/frc                          — returns all records
    // GET /api/expense/frc?designationIds=1,2,3     — returns filtered records
    @GetMapping
    public ResponseEntity<Map<String, Object>> getFareRateCards(
            @RequestParam(required = false) List<Long> designationIds) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<FareRateCardResponseDTO> cards = fareRateCardService.getFareRateCards(designationIds);
            response.put("success", true);
            response.put("data", cards);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch fare rate cards: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // update a single FRC record — triggered when admin clicks Edit in the table.
    // PUT /api/expense/frc/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateFareRateCard(
            @PathVariable Long id,
            @RequestBody FareRateCardUpdateDTO dto) {

        Map<String, Object> response = new HashMap<>();
        try {
            FareRateCardResponseDTO updated = fareRateCardService.updateFareRateCard(id, dto);
            response.put("success", true);
            response.put("message", "Fare rate card updated successfully.");
            response.put("data", updated);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to update fare rate card: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // delete a single FRC record — triggered when admin clicks Delete in the table.
    // DELETE /api/expense/frc/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteFareRateCard(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            fareRateCardService.deleteFareRateCard(id);
            response.put("success", true);
            response.put("message", "Fare rate card deleted successfully.");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to delete fare rate card: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}