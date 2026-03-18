package com.mrreporting.backend.repository;

import com.mrreporting.backend.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByEmail(String email);
    boolean existsByMobile(String mobile);

    List<Employee> findByDesignationId(Long designationId);
    List<Employee> findByStateIdAndDistrictId(Integer stateId, Integer districtId);
}