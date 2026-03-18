package com.mrreporting.backend.repository;

import com.mrreporting.backend.entity.DoctorVisitLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorVisitLocationRepository extends JpaRepository<DoctorVisitLocation, Long> {
}