package com.mrreporting.backend.repository;

import com.mrreporting.backend.dto.ApprovalSummaryDTO;
import com.mrreporting.backend.entity.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AreaRepository extends JpaRepository<Area, Long> {
    List<Area> findByIsActiveTrue();

    @Query("SELECT a FROM Area a WHERE " +
            "a.isActive = true AND " +
            "(:stateId IS NULL OR a.state.id = :stateId) AND " +
            "(:districtId IS NULL OR a.district.id = :districtId) AND " +
            "(:employeeId IS NULL OR a.employee.id = :employeeId)")
    List<Area> findFilteredAreas(@Param("stateId") Integer stateId,
                                 @Param("districtId") Integer districtId,
                                 @Param("employeeId") Long employeeId);

    @Query("SELECT a FROM Area a WHERE a.isActive = true AND a.employee.id = :employeeId")
    List<Area> findAreasByEmployeeId(@Param("employeeId") Long employeeId);

    @Query("SELECT new com.mrreporting.backend.dto.ApprovalSummaryDTO(" +
            "a.state.stateName, a.district.districtName, a.employee.name, a.employee.id, COUNT(a)) " +
            "FROM Area a " +
            "WHERE a.isActive = :isActive AND a.requestStatus = :status " +
            "GROUP BY a.state.stateName, a.district.districtName, a.employee.name, a.employee.id")
    List<ApprovalSummaryDTO> getAreaSummary(@Param("isActive") Boolean isActive,
                                            @Param("status") String status);

    // Counts pending additions (where isActive = false)
    long countByIsActiveFalseAndRequestStatus(String requestStatus);

    // Counts pending deletions (where isActive = true)
    long countByIsActiveTrueAndRequestStatus(String requestStatus);

    List<Area> findByEmployeeIdAndIsActiveAndRequestStatus(
            Long employeeId,
            Boolean isActive,
            String requestStatus
    );

}