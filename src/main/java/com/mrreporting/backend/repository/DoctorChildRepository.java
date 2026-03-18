package com.mrreporting.backend.repository;

import com.mrreporting.backend.entity.DoctorChild;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorChildRepository extends JpaRepository<DoctorChild, Long> {
}