package com.mrreporting.backend.controller;

import com.mrreporting.backend.dto.EmployeeDTO;
import com.mrreporting.backend.entity.*;
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
    @GetMapping
    public ResponseEntity<Map<String, Object>> getEligibleManagers() {
        // Assuming ID 1 or 2 is your Manager designation. Adjust this ID to match your DB!
        Long managerDesignationId = 2L;
        List<Employee> managers = employeeService.getEmployeesByDesignation(managerDesignationId);

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
    public ResponseEntity<List<Employee>> getEmployeesByDesignation(@PathVariable Long designationId) {
        List<Employee> managers = employeeService.getEmployeesByDesignation(designationId);
        return ResponseEntity.ok(managers);
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
}