package com.mrreporting.backend.controller;

import com.mrreporting.backend.dto.EmployeeDTO;
import com.mrreporting.backend.dto.ChangeHqDTO;
import com.mrreporting.backend.dto.MapHierarchyDTO;
import com.mrreporting.backend.dto.DropdownOptionDTO;
import com.mrreporting.backend.entity.*;
import com.mrreporting.backend.service.DistrictService;
import com.mrreporting.backend.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/masters/employees")
@CrossOrigin(origins = "http://localhost:5173")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    // POST /api/masters/employees
    @PostMapping
    public ResponseEntity<?> createEmployee(@RequestBody EmployeeDTO employeeDTO) {
        try {
            Employee employee = convertToEntity(employeeDTO);
            Employee savedEmployee = employeeService.saveEmployee(employee, employeeDTO.getPassword());
            return ResponseEntity.ok(savedEmployee);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET /api/masters/employees
    // dynamically fetches all ASM, RSM, ZSM, and NSM roles
    @GetMapping
    public ResponseEntity<Map<String, Object>> getEligibleManagers() {
        List<Employee> managers = employeeService.getAllReportingManagers();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", managers);

        return ResponseEntity.ok(response);
    }

    private Employee convertToEntity(EmployeeDTO dto) {
        Employee employee = new Employee();

        // Basic Info
        employee.setName(dto.getName());
        employee.setEmail(dto.getEmail());
        employee.setMobile(dto.getMobile());
        employee.setDob(dto.getDob());
        employee.setGender(dto.getGender());
        employee.setReligion(dto.getReligion());
        employee.setAadhar(dto.getAadhar());
        employee.setPan(dto.getPan());

        // Address & Banking
        employee.setAddress(dto.getAddress());
        employee.setBankName(dto.getBankName());
        employee.setBankAccountNumber(dto.getBankAccountNumber());
        employee.setIfscCode(dto.getIfscCode());

        // Company Info
        employee.setDateOfJoining(dto.getDateOfJoining());
        employee.setDateOfReporting(dto.getDateOfReporting());
        employee.setDateOfConfirmation(dto.getDateOfConfirmation());
        employee.setUserCode(dto.getUserCode());

        // --- Relational IDs ---
        if (dto.getStateId() != null) {
            State state = new State();
            state.setId(dto.getStateId());
            employee.setState(state);
        }

        if (dto.getDistrictId() != null) {
            District district = new District();
            district.setId(dto.getDistrictId());
            employee.setDistrict(district);
        }

        if (dto.getDesignationId() != null) {
            Designation designation = new Designation();
            // Assuming Designation ID is Long in entity, so we convert the Integer
            designation.setId(dto.getDesignationId().longValue());
            employee.setDesignation(designation);
        }

        if (dto.getReportingManagerId() != null) {
            Employee manager = new Employee();
            manager.setId(dto.getReportingManagerId());
            employee.setReportingManager(manager);
        }

        return employee;
    }

    @GetMapping("/designation/{designationId}")
    public ResponseEntity<Map<String, Object>> getEmployeesByDesignation(@PathVariable Long designationId) {
        // 1. Fetch the data from the service
        List<Employee> managers = employeeService.getEmployeesByDesignation(designationId);

        // 2. Wrap it in the format the frontend expects
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", managers);

        // 3. Send the wrapped response back
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter")
    public ResponseEntity<Map<String, Object>> getEmployeesByLocation(
            @RequestParam Integer stateId,
            @RequestParam Integer districtId) {

        List<Employee> employees = employeeService.getEmployeesByLocation(stateId, districtId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", employees);

        return ResponseEntity.ok(response);
    }

    // PUT /api/masters/employees/change-hq
    @PutMapping("/change-hq")
    public ResponseEntity<Map<String, Object>> changeHeadquarters(@RequestBody ChangeHqDTO changeHqDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            Employee updatedEmployee = employeeService.changeEmployeeHeadquarters(changeHqDTO);
            response.put("success", true);
            response.put("message", "Employee headquarters changed successfully");
            response.put("data", updatedEmployee);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to change headquarters: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/by-states")
    public ResponseEntity<Map<String, Object>> getEmployeesByStates(
            @RequestParam List<Integer> stateIds) {
        try {
            List<Employee> employees = employeeService.getEmployeesByStates(stateIds);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", employees);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch employees by states: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    // Endpoint for Left Table
    @GetMapping("/hierarchy/employees")
    public ResponseEntity<Map<String, Object>> getHierarchyEmployees(
            @RequestParam List<Integer> stateIds,
            @RequestParam Long designationId) {

        List<Employee> employees = employeeService.getEmployeesForMapping(stateIds, designationId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", employees);

        return ResponseEntity.ok(response);
    }

    // Endpoint for Right Table
    @GetMapping("/hierarchy/managers")
    public ResponseEntity<Map<String, Object>> getHierarchyManagers(
            @RequestParam List<Integer> stateIds,
            @RequestParam Integer level) {

        List<Employee> managers = employeeService.getHigherLevelManagers(stateIds, level);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", managers);

        return ResponseEntity.ok(response);
    }

    // Endpoint for "Map Hierarchy" Save Button
    @PutMapping("/hierarchy/map")
    public ResponseEntity<Map<String, Object>> mapHierarchy(@RequestBody MapHierarchyDTO dto) {
        try {
            employeeService.mapHierarchy(dto.getEmployeeIds(), dto.getManagerId());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Hierarchy updated successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Mapping failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/hierarchy/mrs-for-deletion")
    public ResponseEntity<Map<String, Object>> getMRsForDeletion(@RequestBody Map<String, List<Long>> payload) {
        try {
            List<Long> employeeIds = payload.get("employeeIds");
            List<Employee> mrs = employeeService.getMRsForHierarchyDeletion(employeeIds);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", mrs);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch MRs: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PutMapping("/hierarchy/remove")
    public ResponseEntity<Map<String, Object>> removeHierarchy(@RequestBody Map<String, List<Long>> payload) {
        try {
            List<Long> employeeIds = payload.get("employeeIds");
            employeeService.removeHierarchyForMRs(employeeIds);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Hierarchy removed successfully for selected employees.");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to remove hierarchy: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/filter-multi")
    public ResponseEntity<Map<String, Object>> getEmployeesByMultiFilter(
            @RequestParam List<Integer> stateIds,
            @RequestParam List<Long> designationIds) {
        try {
            List<Employee> employees = employeeService.getEmployeesByMultiFilter(stateIds, designationIds);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", employees);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch employees: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // Returns employees matching the selected districts AND designations.
    @GetMapping("/crm-filter")
    public ResponseEntity<Map<String, Object>> getEmployeesForCrmMapping(
            @RequestParam List<Integer> districtIds,
            @RequestParam(required = false) List<Long> designationIds) {
        try {
            // if no designation selected yet, return empty list instead of throwing 400
            if (designationIds == null || designationIds.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", List.of());
                return ResponseEntity.ok(response);
            }

            List<Employee> employees =
                    employeeService.getEmployeesByDistrictAndDesignation(districtIds, designationIds);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", employees);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch employees: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/by-districts")
    public ResponseEntity<Map<String, Object>> getEmployeesByDistricts(
            @RequestParam List<Integer> districtIds) {
        try {
            List<DropdownOptionDTO> employees =
                    employeeService.getActiveEmployeesByDistricts(districtIds);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", employees);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch employees by districts: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }





}