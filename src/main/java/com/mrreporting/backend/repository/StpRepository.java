package com.mrreporting.backend.repository;

import com.mrreporting.backend.dto.StpEmployeeSummaryDTO;
import com.mrreporting.backend.dto.StpGeographySummaryDTO;
import com.mrreporting.backend.entity.Stp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StpRepository extends JpaRepository<Stp, Long> {

    //only returns approved STPs
    List<Stp> findByEmployeeIdAndIsActiveTrue(Long employeeId);

    // Level 1 — Geography Summary
    // Groups pending STPs by State + District (HQ)
    @Query("SELECT new com.mrreporting.backend.dto.StpGeographySummaryDTO(" +
            "s.employee.state.stateName, " +
            "s.employee.district.districtName, " +
            "s.employee.district.id, " +
            "COUNT(DISTINCT s.employee.id), " +
            "COUNT(s.id)) " +
            "FROM Stp s " +
            "WHERE s.isActive = false AND s.requestStatus = 'PENDING' " +
            "GROUP BY s.employee.state.stateName, " +
            "         s.employee.district.districtName, " +
            "         s.employee.district.id " +
            "ORDER BY s.employee.state.stateName, s.employee.district.districtName")
    List<StpGeographySummaryDTO> getGeographySummary();

    // Level 2 — Employee Summary for a specific district (HQ)
    // Returns: employeeId, name, designation, HQ, pending count

    @Query("SELECT new com.mrreporting.backend.dto.StpEmployeeSummaryDTO(" +
            "s.employee.id, " +
            "s.employee.name, " +
            "s.employee.designation.name, " +
            "s.employee.district.districtName, " +
            "COUNT(s.id)) " +
            "FROM Stp s " +
            "WHERE s.isActive = false " +
            "AND s.requestStatus = 'PENDING' " +
            "AND s.employee.district.id = :districtId " +
            "GROUP BY s.employee.id, " +
            "         s.employee.name, " +
            "         s.employee.designation.name, " +
            "         s.employee.district.districtName " +
            "ORDER BY s.employee.name")
    List<StpEmployeeSummaryDTO> getEmployeeSummaryByDistrict(@Param("districtId") Integer districtId);

    // Level 3 — Actual STP route records for one employee
    // Filtered by requestStatus ("PENDING", "APPROVED", etc.)
    List<Stp> findByEmployeeIdAndRequestStatus(Long employeeId, String requestStatus);

    // Approval Dashboard count (used by existing counts API)
    long countByIsActiveFalseAndRequestStatus(String requestStatus);
}