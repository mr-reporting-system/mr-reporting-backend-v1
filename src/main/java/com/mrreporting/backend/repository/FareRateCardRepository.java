package com.mrreporting.backend.repository;

import com.mrreporting.backend.entity.FareRateCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FareRateCardRepository extends JpaRepository<FareRateCard, Long> {

    // fetch all FRC records for a specific designation
    List<FareRateCard> findByDesignationId(Long designationId);

    // fetch all FRC records for multiple designations, used for filtered table view
    List<FareRateCard> findByDesignationIdIn(List<Long> designationIds);
}