package com.mrreporting.backend.controller;

import com.mrreporting.backend.dto.CrmDoctorResponseDTO;
import com.mrreporting.backend.service.CrmDoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/masters/crm/doctors")
@CrossOrigin(origins = "http://localhost:5173")
public class CrmDoctorController {

    @Autowired
    private CrmDoctorService crmDoctorService;

    // GET /api/masters/crm/doctors?employeeId=5&crmStatus=Not+Linked
    // GET /api/masters/crm/doctors?employeeId=5&crmStatus=Linked
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDoctorsByCrmStatus(
            @RequestParam Long employeeId,
            @RequestParam String crmStatus) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<CrmDoctorResponseDTO> doctors =
                    crmDoctorService.getDoctorsByCrmStatus(employeeId, crmStatus);

            response.put("success", true);
            response.put("data", doctors);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch doctors: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // GET /api/masters/crm/doctors/count?employeeId=5
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getLinkedDoctorCount(
            @RequestParam Long employeeId) {

        Map<String, Object> response = new HashMap<>();
        try {
            long count = crmDoctorService.getLinkedDoctorCount(employeeId);

            response.put("success", true);
            response.put("data", count);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch linked doctor count: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // PUT /api/masters/crm/doctors/link?employeeId=5
    // body: [1, 2, 3]  — frontend sends a raw array directly, no wrapper object
    @PutMapping("/link")
    public ResponseEntity<Map<String, Object>> linkDoctors(
            @RequestParam Long employeeId,
            @RequestBody List<Long> doctorIds) {

        Map<String, Object> response = new HashMap<>();
        try {
            int updatedCount = crmDoctorService.linkDoctors(employeeId, doctorIds);

            response.put("success", true);
            response.put("message", updatedCount + " doctor(s) successfully linked with CRM.");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to link doctors: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // PUT /api/masters/crm/doctors/unlink?employeeId=5
    // body: [1, 2]  — frontend sends a raw array directly, no wrapper object
    @PutMapping("/unlink")
    public ResponseEntity<Map<String, Object>> unlinkDoctors(
            @RequestParam Long employeeId,
            @RequestBody List<Long> doctorIds) {

        Map<String, Object> response = new HashMap<>();
        try {
            int updatedCount = crmDoctorService.unlinkDoctors(employeeId, doctorIds);

            response.put("success", true);
            response.put("message", updatedCount + " doctor(s) successfully unlinked from CRM.");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to unlink doctors: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}