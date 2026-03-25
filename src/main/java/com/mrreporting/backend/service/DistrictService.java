package com.mrreporting.backend.service;

import com.mrreporting.backend.entity.District;
import com.mrreporting.backend.entity.State;
import com.mrreporting.backend.repository.DistrictRepository;
import com.mrreporting.backend.repository.StateRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DistrictService {

    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private StateRepository stateRepository;

    // returns only active districts for a single state, used in most master creation forms
    public List<District> getActiveDistrictsByState(Integer stateId) {
        return districtRepository.findByStateIdAndIsActiveTrue(stateId);
    }

    // returns all districts for a single state regardless of active status, used in employee creation
    public List<District> getAllDistrictsByState(Integer stateId) {
        return districtRepository.findByStateId(stateId);
    }

    // returns active districts for multiple states at once, used in CRM and tour program filters
    public List<District> getActiveDistrictsByStates(List<Integer> stateIds) {
        if (stateIds == null || stateIds.isEmpty()) {
            return List.of();
        }
        return districtRepository.findByStateIdInAndIsActiveTrue(stateIds);
    }

    @Transactional
    public void updateDistrictStatus(List<Integer> districtIds, boolean isActive) {
        List<District> districts = districtRepository.findAllById(districtIds);
        for (District district : districts) {
            district.setIsActive(isActive);
        }
        districtRepository.saveAll(districts);
    }

    @Transactional
    public District createDistrict(Integer stateId, String districtName) {
        State state = stateRepository.findById(stateId)
                .orElseThrow(() -> new RuntimeException("State not found"));

        District district = new District();
        district.setDistrictName(districtName);
        district.setState(state);
        district.setIsActive(false);

        return districtRepository.save(district);
    }

    @Transactional
    public void deleteDistrict(Integer id) {
        District district = districtRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("District not found"));

        if (district.getIsActive() != null && district.getIsActive()) {
            throw new RuntimeException("Cannot delete a district that is currently mapped (Active). Please unmap it first.");
        }

        districtRepository.delete(district);
    }
}