package com.mrreporting.backend.repository;

import com.mrreporting.backend.entity.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AreaRepository extends JpaRepository<Area, Long> {
    @Query("SELECT a FROM Area a WHERE " +
            "(:stateId IS NULL OR a.state.id = :stateId) AND " +
            "(:districtId IS NULL OR a.district.id = :districtId) AND " +
            "(:employeeId IS NULL OR a.employee.id = :employeeId)")
    List<Area> findFilteredAreas(@Param("stateId") Integer stateId,
                                 @Param("districtId") Integer districtId,
                                 @Param("employeeId") Long employeeId);
}