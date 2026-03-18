package com.mrreporting.backend.repository;

import com.mrreporting.backend.entity.DoctorVisitSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorVisitSlotRepository extends JpaRepository<DoctorVisitSlot, Long> {
}