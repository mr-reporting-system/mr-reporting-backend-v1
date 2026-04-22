package com.mrreporting.backend.repository;

import com.mrreporting.backend.entity.DcrReport;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DcrReportRepository extends JpaRepository<DcrReport, Long> {

    @EntityGraph(attributePaths = {"calls"})
    @Query("""
            SELECT DISTINCT r
            FROM DcrReport r
            LEFT JOIN FETCH r.calls c
            WHERE r.employee.id IN :employeeIds
              AND r.reportDate BETWEEN :fromDate AND :toDate
            """)
    List<DcrReport> findByEmployeeIdsAndDateRangeWithCalls(
            @Param("employeeIds") List<Long> employeeIds,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    @EntityGraph(attributePaths = {"calls"})
    @Query("""
            SELECT DISTINCT r
            FROM DcrReport r
            LEFT JOIN FETCH r.calls c
            WHERE r.employee.id = :employeeId
              AND r.reportDate BETWEEN :fromDate AND :toDate
            """)
    List<DcrReport> findByEmployeeIdAndDateRangeWithCalls(
            @Param("employeeId") Long employeeId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );
}
