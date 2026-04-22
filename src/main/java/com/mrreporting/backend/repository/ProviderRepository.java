package com.mrreporting.backend.repository;

import com.mrreporting.backend.dto.ApprovalSummaryDTO;
import com.mrreporting.backend.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {

    @Query("SELECT new com.mrreporting.backend.dto.ApprovalSummaryDTO(" +
            "p.state.stateName, p.district.districtName, p.employee.name, p.employee.id, COUNT(p)) " +
            "FROM Provider p " +
            "WHERE p.isActive = :isActive AND p.requestStatus = :status " +
            "GROUP BY p.state.stateName, p.district.districtName, p.employee.name, p.employee.id")
    List<ApprovalSummaryDTO> getProviderSummary(@Param("isActive") Boolean isActive,
                                                @Param("status") String status);

    List<Provider> findByTypeAndIsActiveTrue(String type);

    List<Provider> findByAreaIdAndIsActiveTrue(Long areaId);

    List<Provider> findByAreaIdAndTypeAndIsActiveTrue(Long areaId, String type);

    long countByIsActiveFalseAndRequestStatus(String requestStatus);

    long countByIsActiveTrueAndRequestStatus(String requestStatus);

    List<Provider> findByEmployeeIdAndIsActiveAndRequestStatus(
            Long employeeId,
            Boolean isActive,
            String requestStatus
    );

    List<Provider> findByEmployeeIdAndTypeIgnoreCaseAndIsActiveTrueOrderByProviderNameAsc(
            Long employeeId,
            String type
    );

    Optional<Provider> findByIdAndTypeIgnoreCaseAndIsActiveTrue(Long id, String type);

    List<Provider> findByEmployeeIdAndStateIdAndDistrictIdAndTypeIgnoreCaseAndIsActiveTrueOrderByProviderNameAsc(
            Long employeeId,
            Integer stateId,
            Integer districtId,
            String type
    );

    List<Provider> findByEmployeeIdAndIsActiveTrueOrderByProviderNameAsc(Long employeeId);

    Optional<Provider> findByIdAndIsActiveTrue(Long id);

    List<Provider> findByEmployeeIdAndStateIdAndDistrictIdAndIsActiveTrueOrderByProviderNameAsc(
            Long employeeId,
            Integer stateId,
            Integer districtId
    );

    List<Provider> findByEmployeeIdInAndIsActiveTrueOrderByProviderNameAsc(List<Long> employeeIds);

    List<Provider> findByStateIdAndDistrictIdInAndTypeIgnoreCaseAndIsActiveTrueOrderByProviderNameAsc(
            Integer stateId,
            List<Integer> districtIds,
            String type
    );
}

