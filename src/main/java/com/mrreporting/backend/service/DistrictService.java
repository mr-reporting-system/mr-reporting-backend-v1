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

    // --- Filtered view for Area/Doctor/Chemist Creation ---
    public List<District> getActiveDistrictsByState(Integer stateId) {
        return districtRepository.findByStateIdAndIsActiveTrue(stateId);
    }

    // --- Global view for Employee Creation 👈 (New method to get ALL for a state) ---
    public List<District> getAllDistrictsByState(Integer stateId) {
        return districtRepository.findByStateId(stateId);
    }

    @Transactional
    public void updateDistrictStatus(List<Integer> districtIds, boolean isActive) {
        List<District> districts = districtRepository.findAllById(districtIds);
        for (District district : districts) {
            district.setIsActive(isActive);
        }
        districtRepository.saveAll(districts);
    }

    // 2. Create a brand new District
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

        // 🛡️ Validation: Prevent deleting active/mapped districts
        if (district.getIsActive() != null && district.getIsActive()) {
            throw new RuntimeException("Cannot delete a district that is currently mapped (Active). Please unmap it first.");
        }

        districtRepository.delete(district);
    }
}