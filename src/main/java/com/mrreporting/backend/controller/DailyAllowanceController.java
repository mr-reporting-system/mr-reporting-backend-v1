package com.mrreporting.backend.controller;

import com.mrreporting.backend.dto.DailyAllowanceCreateDTO;
import com.mrreporting.backend.dto.DailyAllowanceResponseDTO;
import com.mrreporting.backend.dto.DailyAllowanceUpdateDTO;
import com.mrreporting.backend.entity.Designation;
import com.mrreporting.backend.entity.State;
import com.mrreporting.backend.service.DailyAllowanceService;
import com.mrreporting.backend.service.DesignationService;
import com.mrreporting.backend.service.StateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expense/daily-allowance")
@CrossOrigin(origins = "http://localhost:5173")
public class DailyAllowanceController {

    @Autowired
    private DailyAllowanceService dailyAllowanceService;

    @Autowired
    private StateService stateService;

    @Autowired
    private DesignationService designationService;

    // dropdown: active states for the "Select State" multi-select.
    // reuses existing StateService — no new query needed.
    // GET /api/expense/daily-allowance/states
    @GetMapping("/states")
    public ResponseEntity<Map<String, Object>> getActiveStates() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<State> states = stateService.getActiveStates();
            response.put("success", true);
            response.put("data", states);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch states: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // dropdown: all designations for the "Select Designation" multi-select.
    // reuses existing DesignationService — no new query needed.
    // GET /api/expense/daily-allowance/designations
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

    // bulk create DA records.
    // POST /api/expense/daily-allowance
    // body: { stateIds, designationIds, hqDa, exDa, outDa, mobileAllowance, netAllowance, postageStationary, postageFreight }
    // creates stateIds.size() x designationIds.size() records, skipping any duplicates silently.
    @PostMapping
    public ResponseEntity<Map<String, Object>> createDailyAllowances(
            @RequestBody DailyAllowanceCreateDTO dto) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<DailyAllowanceResponseDTO> saved =
                    dailyAllowanceService.createDailyAllowances(dto);

            response.put("success", true);
            response.put("message", saved.size() + " daily allowance rule(s) created successfully.");
            response.put("data", saved);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to create daily allowances: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // fetch all DA records for the bottom table.
    // special allowance is already calculated in the response — frontend renders it directly.
    // GET /api/expense/daily-allowance
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllDailyAllowances() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<DailyAllowanceResponseDTO> records =
                    dailyAllowanceService.getAllDailyAllowances();

            response.put("success", true);
            response.put("data", records);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch daily allowances: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // update a single DA record — triggered when admin clicks the edit icon.
    // PUT /api/expense/daily-allowance/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateDailyAllowance(
            @PathVariable Long id,
            @RequestBody DailyAllowanceUpdateDTO dto) {

        Map<String, Object> response = new HashMap<>();
        try {
            DailyAllowanceResponseDTO updated =
                    dailyAllowanceService.updateDailyAllowance(id, dto);

            response.put("success", true);
            response.put("message", "Daily allowance updated successfully.");
            response.put("data", updated);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to update daily allowance: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // delete a single DA record — triggered when admin clicks the delete icon.
    // DELETE /api/expense/daily-allowance/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteDailyAllowance(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            dailyAllowanceService.deleteDailyAllowance(id);

            response.put("success", true);
            response.put("message", "Daily allowance deleted successfully.");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to delete daily allowance: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}