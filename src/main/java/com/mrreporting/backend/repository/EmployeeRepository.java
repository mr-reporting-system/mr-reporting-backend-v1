package com.mrreporting.backend.repository;

import com.mrreporting.backend.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByEmail(String email);
    boolean existsByMobile(String mobile);

    List<Employee> findByDesignationId(Long designationId);
    List<Employee> findByStateIdAndDistrictId(Integer stateId, Integer districtId);
    List<Employee> findByStateIdIn(List<Integer> stateIds);
    List<Employee> findByDesignationNameIn(List<String> designationNames);
    List<Employee> findByStateIdInAndDesignationId(List<Integer> stateIds, Long designationId);
    List<Employee> findByStateIdInAndDesignationLevelGreaterThan(List<Integer> stateIds, Integer level);
    List<Employee> findByStateIdInAndDesignationIdIn(List<Integer> stateIds, List<Long> designationIds);
    List<Employee> findByDistrictIdInAndDesignationIdIn(List<Integer> districtIds, List<Long> designationIds);

    @Query("SELECT DISTINCT e FROM Employee e WHERE e.designation.name = 'MR' " +
            "AND e.reportingManager IS NOT NULL " +
            "AND (e.id IN :employeeIds OR e.reportingManager.id IN :employeeIds)")
    List<Employee> findMRsByEmployeeOrManagerIds(@Param("employeeIds") List<Long> employeeIds);

    // fetch employees in the given districts, used for geographical tour program filter
    List<Employee> findByDistrictIdIn(List<Integer> districtIds);

    // fetch employees by designation filtered by active status.
    // used to populate the employee dropdown in the hierarchical tour program filter.
    List<Employee> findByDesignationIdAndIsActive(Long designationId, Boolean isActive);

    // fetch specific employees by ids, designation and status.
    // used when admin selects specific employees in hierarchical mode.
    List<Employee> findByIdInAndDesignationIdAndIsActive(
            List<Long> employeeIds,
            Long designationId,
            Boolean isActive
    );

    List<Employee> findByDistrictIdInAndIsActiveTrueOrderByNameAsc(List<Integer> districtIds);

}