package com.mrreporting.backend.service;

import com.mrreporting.backend.dto.ChangeHqDTO;
import com.mrreporting.backend.entity.Designation;
import com.mrreporting.backend.entity.Employee;
import com.mrreporting.backend.entity.User;
import com.mrreporting.backend.entity.State;
import com.mrreporting.backend.entity.District;
import com.mrreporting.backend.repository.DesignationRepository;
import com.mrreporting.backend.repository.EmployeeRepository;
import com.mrreporting.backend.repository.UserRepository;
import com.mrreporting.backend.repository.StateRepository;
import com.mrreporting.backend.repository.DistrictRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DesignationRepository designationRepository;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Employee saveEmployee(Employee employee, String password) {
        // 1. Validation Checks
        if (employeeRepository.existsByEmail(employee.getEmail())) {
            throw new RuntimeException("An employee with this email already exists!");
        }

        if (employeeRepository.existsByMobile(employee.getMobile())) {
            throw new RuntimeException("An employee with this mobile number already exists!");
        }

        // 2. Create and Save the User (Login Credentials)
        User newUser = new User();
        newUser.setEmail(employee.getEmail());
        newUser.setPassword(passwordEncoder.encode(password));

        // 3. Fetch the full Designation to avoid NullPointerException and set the role to UPPERCASE
        if (employee.getDesignation() != null && employee.getDesignation().getId() != null) {
            Designation fullDesignation = designationRepository.findById(employee.getDesignation().getId())
                    .orElseThrow(() -> new RuntimeException("Designation not found in database!"));

            employee.setDesignation(fullDesignation);
            newUser.setRole(fullDesignation.getName().toUpperCase());
        }

        User savedUser = userRepository.save(newUser);

        // 4. Link the Saved User to the Employee
        employee.setUser(savedUser);

        // 5. Activate the assigned State and District 🗺️
        if (employee.getState() != null && employee.getState().getId() != null) {
            State state = stateRepository.findById(employee.getState().getId())
                    .orElseThrow(() -> new RuntimeException("State not found in database!"));
            state.setIsActive(true);
            stateRepository.save(state);
            employee.setState(state); // Update the employee object with the fetched state
        }

        if (employee.getDistrict() != null && employee.getDistrict().getId() != null) {
            District district = districtRepository.findById(employee.getDistrict().getId())
                    .orElseThrow(() -> new RuntimeException("District not found in database!"));
            district.setIsActive(true);
            districtRepository.save(district);
            employee.setDistrict(district); // Update the employee object with the fetched district
        }

        // 6. Save the Employee Profile
        return employeeRepository.save(employee);
    }

    public List<Employee> getEmployeesByDesignation(Long designationId) {
        return employeeRepository.findByDesignationId(designationId);
    }

    public List<Employee> getEmployeesByLocation(Integer stateId, Integer districtId) {
        return employeeRepository.findByStateIdAndDistrictId(stateId, districtId);
    }
    @Transactional // Critical for multi-step database updates
    public Employee changeEmployeeHeadquarters(ChangeHqDTO dto) {

        // Step A: Find the employee in the database
        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + dto.getEmployeeId()));

        // Step B: Find the new State and District entities
        State newState = stateRepository.findById(dto.getStateId())
                .orElseThrow(() -> new RuntimeException("New state not found in database!"));

        District newDistrict = districtRepository.findById(dto.getDistrictId())
                .orElseThrow(() -> new RuntimeException("New district not found in database!"));

        // Step C: Link the new location entities to the employee object
        employee.setState(newState);
        employee.setDistrict(newDistrict);

        // Step D: Save the finalized employee profile
        return employeeRepository.save(employee);
    }
}