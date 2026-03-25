package com.mrreporting.backend.repository;

import com.mrreporting.backend.entity.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistrictRepository extends JpaRepository<District, Integer> {

    // single state, all districts regardless of status
    List<District> findByStateId(Integer stateId);

    // single state, active districts only
    List<District> findByStateIdAndIsActiveTrue(Integer stateId);

    // multiple states, active districts only — used in CRM and tour program filters
    List<District> findByStateIdInAndIsActiveTrue(List<Integer> stateIds);
}