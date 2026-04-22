package com.mrreporting.backend.repository;

import com.mrreporting.backend.dto.StockistMappingReportRowDTO;
import com.mrreporting.backend.entity.EmployeeStockistMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeStockistMappingRepository extends JpaRepository<EmployeeStockistMapping, Long> {

    List<EmployeeStockistMapping> findByStockistId(Long stockistId);

    List<EmployeeStockistMapping> findByStockistIdIn(List<Long> stockistIds);

    List<EmployeeStockistMapping> findByEmployeeIdIn(List<Long> employeeIds);

    void deleteByStockistId(Long stockistId);

    @Query("""
            SELECT new com.mrreporting.backend.dto.StockistMappingReportRowDTO(
                m.id,
                es.id,
                es.stateName,
                ed.id,
                ed.districtName,
                e.id,
                e.name,
                des.name,
                s.id,
                s.providerCode,
                s.providerName,
                ss.id,
                ss.stateName,
                sd.id,
                sd.districtName
            )
            FROM EmployeeStockistMapping m
            JOIN m.employee e
            JOIN e.state es
            JOIN e.district ed
            LEFT JOIN e.designation des
            JOIN m.stockist s
            JOIN s.state ss
            JOIN s.district sd
            WHERE (:skipStateFilter = true OR e.state.id IN :stateIds)
              AND (:skipDistrictFilter = true OR e.district.id IN :districtIds)
              AND (:skipEmployeeFilter = true OR e.id IN :employeeIds)
            ORDER BY e.name ASC, s.providerName ASC
            """)
    List<StockistMappingReportRowDTO> findReportRows(
            @Param("skipStateFilter") boolean skipStateFilter,
            @Param("stateIds") List<Integer> stateIds,
            @Param("skipDistrictFilter") boolean skipDistrictFilter,
            @Param("districtIds") List<Integer> districtIds,
            @Param("skipEmployeeFilter") boolean skipEmployeeFilter,
            @Param("employeeIds") List<Long> employeeIds
    );
}
