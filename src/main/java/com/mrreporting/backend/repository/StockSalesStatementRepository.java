package com.mrreporting.backend.repository;

import com.mrreporting.backend.entity.StockSalesStatement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockSalesStatementRepository extends JpaRepository<StockSalesStatement, Long> {

    Optional<StockSalesStatement> findByStockistIdAndMonthAndYear(
            Long stockistId,
            Integer month,
            Integer year
    );

    @Query("SELECT DISTINCT s FROM StockSalesStatement s " +
            "LEFT JOIN FETCH s.items i " +
            "LEFT JOIN FETCH i.product p " +
            "WHERE s.month = :month " +
            "AND s.year = :year " +
            "AND (:skipStateFilter = true OR s.state.id IN :stateIds) " +
            "AND (:skipDistrictFilter = true OR s.district.id IN :districtIds) " +
            "AND (:skipEmployeeFilter = true OR s.employee.id IN :employeeIds) " +
            "AND (:skipProviderFilter = true OR s.stockist.id IN :providerIds)")
    List<StockSalesStatement> findForSssViewReport(
            @Param("month") Integer month,
            @Param("year") Integer year,
            @Param("skipStateFilter") boolean skipStateFilter,
            @Param("stateIds") List<Integer> stateIds,
            @Param("skipDistrictFilter") boolean skipDistrictFilter,
            @Param("districtIds") List<Integer> districtIds,
            @Param("skipEmployeeFilter") boolean skipEmployeeFilter,
            @Param("employeeIds") List<Long> employeeIds,
            @Param("skipProviderFilter") boolean skipProviderFilter,
            @Param("providerIds") List<Long> providerIds
    );
}
