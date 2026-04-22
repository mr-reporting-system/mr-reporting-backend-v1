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
    List<Employee> findByDistrictIdIn(List<Integer> districtIds);
    List<Employee> findByDesignationIdAndIsActive(Long designationId, Boolean isActive);
    List<Employee> findByIdInAndDesignationIdAndIsActive(
            List<Long> employeeIds,
            Long designationId,
            Boolean isActive
    );
    List<Employee> findByDistrictIdInAndIsActiveTrueOrderByNameAsc(List<Integer> districtIds);

    List<Employee> findByStateIdAndDistrictIdInAndIsActiveTrueOrderByNameAsc(
            Integer stateId,
            List<Integer> districtIds
    );

    @Query("SELECT DISTINCT e FROM Employee e WHERE e.designation.name = 'MR' " +
            "AND e.reportingManager IS NOT NULL " +
            "AND (e.id IN :employeeIds OR e.reportingManager.id IN :employeeIds)")
    List<Employee> findMRsByEmployeeOrManagerIds(@Param("employeeIds") List<Long> employeeIds);

    @Query("""
        SELECT e
        FROM Employee e
        JOIN e.designation d
        WHERE e.state.id = :stateId
          AND e.district.id = :districtId
          AND e.isActive = true
          AND UPPER(d.name) = 'MR'
        ORDER BY e.name ASC
        """)
    List<Employee> findActiveMrEmployeesByLocation(
            @Param("stateId") Integer stateId,
            @Param("districtId") Integer districtId
    );

    @Query("""
        SELECT DISTINCT e
        FROM Employee e
        JOIN e.designation d
        WHERE e.id IN :employeeIds
          AND e.isActive = true
          AND UPPER(d.name) = 'MR'
        """)
    List<Employee> findActiveMrEmployeesByIds(
            @Param("employeeIds") List<Long> employeeIds
    );

    @Query("""
    SELECT DISTINCT e
    FROM Employee e
    JOIN e.designation d
    WHERE e.isActive = true
      AND UPPER(d.name) = 'MR'
      AND (:skipStateFilter = true OR e.state.id IN :stateIds)
      AND (:skipDistrictFilter = true OR e.district.id IN :districtIds)
      AND (:skipEmployeeFilter = true OR e.id IN :employeeIds)
    ORDER BY e.name ASC
    """)
    List<Employee> findActiveMrEmployeesForTargetReport(
            @Param("skipStateFilter") boolean skipStateFilter,
            @Param("stateIds") List<Integer> stateIds,
            @Param("skipDistrictFilter") boolean skipDistrictFilter,
            @Param("districtIds") List<Integer> districtIds,
            @Param("skipEmployeeFilter") boolean skipEmployeeFilter,
            @Param("employeeIds") List<Long> employeeIds
    );

    @Query("""
        SELECT DISTINCT e
        FROM Employee e
        LEFT JOIN e.reportingManager rm
        WHERE (:isActive IS NULL OR e.isActive = :isActive)
          AND (:skipStateFilter = true OR e.state.id IN :stateIds)
          AND (:skipDistrictFilter = true OR e.district.id IN :districtIds)
        ORDER BY e.name ASC
        """)
    List<Employee> findEmployeesForDcrConsolidateGeographical(
            @Param("isActive") Boolean isActive,
            @Param("skipStateFilter") boolean skipStateFilter,
            @Param("stateIds") List<Integer> stateIds,
            @Param("skipDistrictFilter") boolean skipDistrictFilter,
            @Param("districtIds") List<Integer> districtIds
    );

    @Query("""
        SELECT DISTINCT e
        FROM Employee e
        JOIN e.reportingManager rm
        WHERE (:isActive IS NULL OR e.isActive = :isActive)
          AND (:skipStateFilter = true OR rm.state.id IN :stateIds)
          AND (:skipDistrictFilter = true OR rm.district.id IN :districtIds)
        ORDER BY e.name ASC
        """)
    List<Employee> findEmployeesForDcrConsolidateHierarchical(
            @Param("isActive") Boolean isActive,
            @Param("skipStateFilter") boolean skipStateFilter,
            @Param("stateIds") List<Integer> stateIds,
            @Param("skipDistrictFilter") boolean skipDistrictFilter,
            @Param("districtIds") List<Integer> districtIds
    );


}
