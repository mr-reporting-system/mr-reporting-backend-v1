package com.mrreporting.backend.repository;

import com.mrreporting.backend.entity.DailyAllowance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DailyAllowanceRepository extends JpaRepository<DailyAllowance, Long> {

    // used before insert to check if a rule for this state+designation already exists
    Optional<DailyAllowance> findByStateIdAndDesignationId(Integer stateId, Long designationId);

    // fetch all DA records for a specific state, used for optional table filtering
    List<DailyAllowance> findByStateIdIn(List<Integer> stateIds);
}