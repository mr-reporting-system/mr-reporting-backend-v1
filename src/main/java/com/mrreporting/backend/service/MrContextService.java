package com.mrreporting.backend.service;

import com.mrreporting.backend.entity.Area;
import com.mrreporting.backend.entity.Employee;
import com.mrreporting.backend.entity.User;
import com.mrreporting.backend.repository.AreaRepository;
import com.mrreporting.backend.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MrContextService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AreaRepository areaRepository;

    @Transactional(readOnly = true)
    public Employee getCurrentMr() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User currentUser)) {
            throw new RuntimeException("Unauthorized user.");
        }

        Employee employee = employeeRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Employee profile not found for the logged-in user."));

        if (!Boolean.TRUE.equals(employee.getIsActive())) {
            throw new RuntimeException("Inactive employees cannot access MR master creation flows.");
        }
        if (employee.getDesignation() == null || !"MR".equalsIgnoreCase(cleanNullable(employee.getDesignation().getName()))) {
            throw new RuntimeException("Only MR users can access this functionality.");
        }
        if (employee.getState() == null || employee.getDistrict() == null) {
            throw new RuntimeException("Logged-in MR must have state and district mapped.");
        }

        return employee;
    }

    @Transactional(readOnly = true)
    public Area getOwnedActiveArea(Long areaId) {
        Employee employee = getCurrentMr();
        return getOwnedActiveArea(areaId, employee);
    }

    @Transactional(readOnly = true)
    public Area getOwnedActiveArea(Long areaId, Employee employee) {
        if (areaId == null) {
            throw new RuntimeException("areaId is required.");
        }

        Area area = areaRepository.findById(areaId)
                .orElseThrow(() -> new RuntimeException("Area not found with id: " + areaId));

        if (!Boolean.TRUE.equals(area.getIsActive())) {
            throw new RuntimeException("Inactive area cannot be used.");
        }
        if ("DELETION".equalsIgnoreCase(cleanNullable(area.getRequestStatus()))) {
            throw new RuntimeException("Area is pending deletion and cannot be used.");
        }
        if (area.getEmployee() == null || !employee.getId().equals(area.getEmployee().getId())) {
            throw new RuntimeException("Selected area does not belong to the logged-in MR.");
        }

        return area;
    }

    private String cleanNullable(String value) {
        return value == null ? null : value.trim();
    }
}
