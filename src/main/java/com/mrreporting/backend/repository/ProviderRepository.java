package com.mrreporting.backend.repository;

import com.mrreporting.backend.dto.ApprovalSummaryDTO;
import com.mrreporting.backend.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {

    @Query("SELECT new com.mrreporting.backend.dto.ApprovalSummaryDTO(" +
            "p.state.stateName, p.district.districtName, p.employee.name, p.employee.id, COUNT(p)) " +
            "FROM Provider p " +
            "WHERE p.isActive = :isActive AND p.requestStatus = :status " +
            "GROUP BY p.state.stateName, p.district.districtName, p.employee.name, p.employee.id")
    List<ApprovalSummaryDTO> getProviderSummary(@Param("isActive") Boolean isActive,
                                                @Param("status") String status);

    //  Only approved providers by type
    List<Provider> findByTypeAndIsActiveTrue(String type);

    //  Approved providers in an area
    List<Provider> findByAreaIdAndIsActiveTrue(Long areaId);

    //  Approved providers by area and type (Chemist vs Stockist)
    List<Provider> findByAreaIdAndTypeAndIsActiveTrue(Long areaId, String type);

    // Counts pending Provider additions
    long countByIsActiveFalseAndRequestStatus(String requestStatus);

    // Counts pending Provider deletions
    long countByIsActiveTrueAndRequestStatus(String requestStatus);

    List<Provider> findByEmployeeIdAndIsActiveAndRequestStatus(
            Long employeeId,
            Boolean isActive,
            String requestStatus
    );
}