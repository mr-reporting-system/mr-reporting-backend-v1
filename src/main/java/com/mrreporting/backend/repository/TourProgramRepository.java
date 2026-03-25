package com.mrreporting.backend.repository;

import com.mrreporting.backend.entity.TourProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TourProgramRepository extends JpaRepository<TourProgram, Long> {

    // find a specific employee's plan for a given month and year
    Optional<TourProgram> findByEmployeeIdAndMonthAndYear(Long employeeId, Integer month, Integer year);

    // fetch all plans for a list of employees for a given month and year.
    // used after we resolve the employee list from the geographical or hierarchical filter.
    @Query("SELECT tp FROM TourProgram tp " +
            "WHERE tp.employee.id IN :employeeIds " +
            "AND tp.month = :month " +
            "AND tp.year = :year")
    List<TourProgram> findByEmployeeIdsAndMonthAndYear(
            @Param("employeeIds") List<Long> employeeIds,
            @Param("month") Integer month,
            @Param("year") Integer year
    );

    // count of submitted but not yet approved plans, used for approval dashboard badge
    long countByIsSubmittedTrueAndIsApprovedFalse();
}