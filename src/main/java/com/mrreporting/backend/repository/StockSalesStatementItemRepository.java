package com.mrreporting.backend.repository;

import com.mrreporting.backend.entity.StockSalesStatementItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockSalesStatementItemRepository extends JpaRepository<StockSalesStatementItem, Long> {
}
