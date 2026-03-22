package com.mrreporting.backend.repository;

import com.mrreporting.backend.dto.ApprovalSummaryDTO;
import com.mrreporting.backend.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    @Query("SELECT new com.mrreporting.backend.dto.ApprovalSummaryDTO(" +
            "d.state.stateName, d.district.districtName, d.employee.name, d.employee.id, COUNT(d)) " +
            "FROM Doctor d " +
            "WHERE d.isActive = :isActive AND d.requestStatus = :status " +
            "GROUP BY d.state.stateName, d.district.districtName, d.employee.name, d.employee.id")
    List<ApprovalSummaryDTO> getDoctorSummary(@Param("isActive") Boolean isActive,
                                              @Param("status") String status);

    // Only fetch approved doctors
    List<Doctor> findByIsActiveTrue();

    // Only fetch approved doctors for a specific area
    List<Doctor> findByAreaIdAndIsActiveTrue(Long areaId);

    // Counts pending Doctor additions
    long countByIsActiveFalseAndRequestStatus(String requestStatus);

    // Counts pending Doctor deletions
    long countByIsActiveTrueAndRequestStatus(String requestStatus);

    List<Doctor> findByEmployeeIdAndIsActiveAndRequestStatus(
            Long employeeId,
            Boolean isActive,
            String requestStatus
    );
}