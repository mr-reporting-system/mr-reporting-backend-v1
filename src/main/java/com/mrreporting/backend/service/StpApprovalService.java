package com.mrreporting.backend.service;

import com.mrreporting.backend.dto.StpEmployeeSummaryDTO;
import com.mrreporting.backend.dto.StpGeographySummaryDTO;
import com.mrreporting.backend.dto.StpRouteDetailDTO;
import com.mrreporting.backend.entity.Stp;
import com.mrreporting.backend.repository.StpRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StpApprovalService {

    @Autowired
    private StpRepository stpRepository;

    // -------------------------------------------------------
    // Level 1 — Geography Summary
    // Called on page load — no filters needed
    // -------------------------------------------------------
    public List<StpGeographySummaryDTO> getGeographySummary() {
        return stpRepository.getGeographySummary();
    }

    // -------------------------------------------------------
    // Level 2 — Employee Summary
    // Called when admin clicks a row in Level 1 table
    // -------------------------------------------------------
    public List<StpEmployeeSummaryDTO> getEmployeeSummary(Integer districtId) {
        if (districtId == null) {
            throw new IllegalArgumentException("districtId is required.");
        }
        return stpRepository.getEmployeeSummaryByDistrict(districtId);
    }

    // Level 3 — STP Route Details for one employee
    public List<StpRouteDetailDTO> getStpDetails(Long employeeId, String requestStatus) {
        if (employeeId == null) {
            throw new IllegalArgumentException("employeeId is required.");
        }

        // Default to "PENDING" if no filter passed
        String status = (requestStatus != null && !requestStatus.isBlank())
                ? requestStatus.toUpperCase()
                : "PENDING";

        List<Stp> stps = stpRepository.findByEmployeeIdAndRequestStatus(employeeId, status);

        return stps.stream()
                .map(this::toRouteDetailDTO)
                .toList();
    }

    // Level 4 — Approve selected STP IDs
    @Transactional
    public int approveStps(List<Long> stpIds) {
        validateIds(stpIds, "approve");

        List<Stp> stps = stpRepository.findAllById(stpIds);

        // Guard: only approve STPs that are genuinely pending
        List<Stp> pendingStps = stps.stream()
                .filter(s -> Boolean.FALSE.equals(s.getIsActive())
                        && "PENDING".equals(s.getRequestStatus()))
                .toList();

        if (pendingStps.isEmpty()) {
            throw new RuntimeException("No pending STPs found for the given IDs.");
        }

        pendingStps.forEach(s -> {
            s.setIsActive(true);
            s.setRequestStatus("APPROVED");
        });

        stpRepository.saveAll(pendingStps);
        return pendingStps.size();
    }

    // Level 4 — Hard delete selected STP IDs
    @Transactional
    public int deleteStps(List<Long> stpIds) {
        validateIds(stpIds, "delete");

        // Confirm all IDs exist before deleting
        List<Stp> stps = stpRepository.findAllById(stpIds);

        if (stps.isEmpty()) {
            throw new RuntimeException("No STPs found for the given IDs.");
        }

        stpRepository.deleteAllById(stpIds);
        return stps.size();
    }

    // Private Helpers
    private StpRouteDetailDTO toRouteDetailDTO(Stp stp) {
        return new StpRouteDetailDTO(
                stp.getId(),
                stp.getFromArea() != null ? stp.getFromArea().getAreaName() : "---",
                stp.getToArea() != null ? stp.getToArea().getAreaName() : "---",
                stp.getDistance(),
                stp.getAreaType(),
                stp.getFrc(),
                stp.getFrequencyVisit(),
                stp.getRequestStatus()
        );
    }

    private void validateIds(List<Long> ids, String action) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("No STP IDs provided to " + action + ".");
        }
    }
}