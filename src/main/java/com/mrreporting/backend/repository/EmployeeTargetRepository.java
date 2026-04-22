package com.mrreporting.backend.repository;

import com.mrreporting.backend.entity.EmployeeTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeTargetRepository extends JpaRepository<EmployeeTarget, Long> {

    List<EmployeeTarget> findByEmployeeIdInAndYear(List<Long> employeeIds, Integer year);

    List<EmployeeTarget> findByEmployeeIdAndYearOrderByMonthAsc(Long employeeId, Integer year);

    boolean existsByEmployeeIdAndYear(Long employeeId, Integer year);

}
