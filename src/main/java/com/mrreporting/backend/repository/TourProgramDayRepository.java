package com.mrreporting.backend.repository;

import com.mrreporting.backend.entity.TourProgramDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TourProgramDayRepository extends JpaRepository<TourProgramDay, Long> {

    // fetch all days for a tour program ordered by date for the calendar detail view
    @Query("SELECT d FROM TourProgramDay d " +
            "WHERE d.tourProgram.id = :tourProgramId " +
            "ORDER BY d.date ASC")
    List<TourProgramDay> findByTourProgramIdOrderByDate(@Param("tourProgramId") Long tourProgramId);
}