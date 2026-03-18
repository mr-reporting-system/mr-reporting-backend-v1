package com.mrreporting.backend.service;

import com.mrreporting.backend.entity.Designation;
import com.mrreporting.backend.entity.Employee;
import com.mrreporting.backend.entity.User;
import com.mrreporting.backend.repository.DesignationRepository;
import com.mrreporting.backend.repository.EmployeeRepository;
import com.mrreporting.backend.repository.UserRepository;
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
    private DesignationRepository designationRepository; // Added to fetch the full designation

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

            // Set the full designation back to the employee so it saves correctly
            employee.setDesignation(fullDesignation);

            // Convert the name to ALL CAPS and save as the user's role
            newUser.setRole(fullDesignation.getName().toUpperCase());
        }

        User savedUser = userRepository.save(newUser);

        // 4. Link the Saved User to the Employee
        employee.setUser(savedUser);

        // 5. Save the Employee Profile
        return employeeRepository.save(employee);
    }

    public List<Employee> getEmployeesByDesignation(Long designationId) {
        return employeeRepository.findByDesignationId(designationId);
    }

    public List<Employee> getEmployeesByLocation(Integer stateId, Integer districtId) {
        return employeeRepository.findByStateIdAndDistrictId(stateId, districtId);
    }
}