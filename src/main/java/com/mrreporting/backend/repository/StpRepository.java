package com.mrreporting.backend.repository;

import com.mrreporting.backend.entity.Stp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StpRepository extends JpaRepository<Stp, Long> {

    //Custom method to fetch all STPs for a specific employee to display in the bottom table
    List<Stp> findByEmployeeId(Long employeeId);
}