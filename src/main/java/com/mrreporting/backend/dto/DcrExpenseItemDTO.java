package com.mrreporting.backend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DcrExpenseItemDTO {
    private String expenseType;
    private BigDecimal amount;
    private String remarks;
}
