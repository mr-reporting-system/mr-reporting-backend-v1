package com.mrreporting.backend.repository;

import com.mrreporting.backend.entity.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AreaRepository extends JpaRepository<Area, Long> {
    // We can add custom search methods here later if needed!
}