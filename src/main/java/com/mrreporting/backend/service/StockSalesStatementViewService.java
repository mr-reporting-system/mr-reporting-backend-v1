package com.mrreporting.backend.service;

import com.mrreporting.backend.dto.DropdownOptionDTO;
import com.mrreporting.backend.dto.SSSViewFilterDTO;
import com.mrreporting.backend.dto.SSSViewReportResponseDTO;
import com.mrreporting.backend.dto.SSSViewReportRowDTO;
import com.mrreporting.backend.entity.*;
import com.mrreporting.backend.repository.EmployeeRepository;
import com.mrreporting.backend.repository.ProviderRepository;
import com.mrreporting.backend.repository.StockSalesStatementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class StockSalesStatementViewService {

    @Autowired
    private StateService stateService;

    @Autowired
    private DistrictService districtService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private StockSalesStatementRepository stockSalesStatementRepository;

    @Transactional(readOnly = true)
    public List<DropdownOptionDTO> getActiveStates() {
        return stateService.getActiveStates().stream()
                .sorted(Comparator.comparing(
                        state -> safeText(state.getStateName()),
                        String.CASE_INSENSITIVE_ORDER
                ))
                .map(state -> new DropdownOptionDTO(
                        Long.valueOf(state.getId()),
                        state.getStateName(),
                        null
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DropdownOptionDTO> getDistrictsByStates(List<Integer> stateIds) {
        return districtService.getActiveDistrictsByStates(stateIds).stream()
                .sorted(Comparator.comparing(
                        district -> safeText(district.getDistrictName()),
                        String.CASE_INSENSITIVE_ORDER
                ))
                .map(district -> new DropdownOptionDTO(
                        Long.valueOf(district.getId()),
                        district.getDistrictName(),
                        null
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DropdownOptionDTO> getEmployeesByDistricts(List<Integer> districtIds) {
        if (districtIds == null || districtIds.isEmpty()) {
            return List.of();
        }

        return employeeRepository.findByDistrictIdInAndIsActiveTrueOrderByNameAsc(districtIds).stream()
                .map(employee -> new DropdownOptionDTO(
                        employee.getId(),
                        employee.getName(),
                        null
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DropdownOptionDTO> getProvidersByEmployees(List<Long> employeeIds) {
        if (employeeIds == null || employeeIds.isEmpty()) {
            return List.of();
        }

        return providerRepository.findByEmployeeIdInAndIsActiveTrueOrderByProviderNameAsc(employeeIds).stream()
                .map(provider -> new DropdownOptionDTO(
                        provider.getId(),
                        provider.getProviderName(),
                        provider.getType()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public SSSViewReportResponseDTO getReport(SSSViewFilterDTO dto) {
        validateFilter(dto);

        List<StockSalesStatement> statements =
                stockSalesStatementRepository.findForSssViewReport(
                        dto.getMonth(),
                        dto.getYear(),
                        isEmpty(dto.getStateIds()),
                        integerIdsOrDummy(dto.getStateIds()),
                        isEmpty(dto.getDistrictIds()),
                        integerIdsOrDummy(dto.getDistrictIds()),
                        isEmpty(dto.getEmployeeIds()),
                        longIdsOrDummy(dto.getEmployeeIds()),
                        isEmpty(dto.getResolvedProviderIds()),
                        longIdsOrDummy(dto.getResolvedProviderIds())
                );

        List<SSSViewReportRowDTO> rows = new ArrayList<>();

        for (StockSalesStatement statement : statements) {
            for (StockSalesStatementItem item : statement.getItems()) {
                rows.add(toRow(statement, item));
            }
        }

        rows.sort(
                Comparator.comparing(
                                (SSSViewReportRowDTO row) -> safeText(row.getPartyName()),
                                String.CASE_INSENSITIVE_ORDER
                        )
                        .thenComparing(
                                row -> safeText(row.getProductName()),
                                String.CASE_INSENSITIVE_ORDER
                        )
        );

        SSSViewReportRowDTO totals = calculateTotals(rows);

        return new SSSViewReportResponseDTO(
                rows,
                totals,
                (long) rows.size()
        );
    }

    private SSSViewReportRowDTO toRow(StockSalesStatement statement, StockSalesStatementItem item) {
        BigDecimal netRate = nullSafeRate(item.getNetRate());

        Integer opening = nullSafeInteger(item.getOpening());
        Integer receipt = nullSafeInteger(item.getReceipt());
        Integer primarySale = nullSafeInteger(item.getPrimarySale());
        Integer scheme = nullSafeInteger(item.getScheme());
        Integer salesReturn = nullSafeInteger(item.getSalesReturn());
        Integer closing = nullSafeInteger(item.getClosing());
        Integer expiry = nullSafeInteger(item.getExpiry());
        Integer breakage = nullSafeInteger(item.getBreakage());
        Integer batchRecall = nullSafeInteger(item.getBatchRecall());
        Integer secondarySale = nullSafeInteger(item.getSecondarySale());

        Integer total = opening + receipt + primarySale + scheme;

        return new SSSViewReportRowDTO(
                statement.getStockist().getId(),
                statement.getStockist().getProviderName(),
                statement.getStockist().getType(),
                item.getProduct().getId(),
                item.getProduct().getProductName(),
                item.getProduct().getProductCode(),
                netRate,
                opening,
                multiply(netRate, opening),
                receipt,
                primarySale,
                multiply(netRate, primarySale),
                scheme,
                total,
                salesReturn,
                multiply(netRate, salesReturn),
                closing,
                multiply(netRate, closing),
                expiry,
                multiply(netRate, expiry),
                breakage,
                multiply(netRate, breakage),
                batchRecall,
                multiply(netRate, batchRecall),
                safeBatchNumber(item.getBatchNumber()),
                secondarySale,
                multiply(netRate, secondarySale)
        );
    }

    private SSSViewReportRowDTO calculateTotals(List<SSSViewReportRowDTO> rows) {
        SSSViewReportRowDTO totals = new SSSViewReportRowDTO();

        totals.setProviderId(null);
        totals.setPartyName("TOTAL");
        totals.setProviderType(null);
        totals.setProductId(null);
        totals.setProductName("");
        totals.setProductCode("");
        totals.setNetRate(BigDecimal.ZERO);

        totals.setOpening(0);
        totals.setOpeningValue(BigDecimal.ZERO);
        totals.setReceipt(0);
        totals.setPrimarySale(0);
        totals.setPrimaryValue(BigDecimal.ZERO);
        totals.setScheme(0);
        totals.setTotal(0);
        totals.setSalesReturn(0);
        totals.setReturnValue(BigDecimal.ZERO);
        totals.setClosing(0);
        totals.setClosingValue(BigDecimal.ZERO);
        totals.setExpiry(0);
        totals.setExpiryValue(BigDecimal.ZERO);
        totals.setBreakage(0);
        totals.setBreakageValue(BigDecimal.ZERO);
        totals.setBatchRecall(0);
        totals.setBatchRecallValue(BigDecimal.ZERO);
        totals.setBatchNumber("--");
        totals.setSecondarySale(0);
        totals.setSecondarySaleValue(BigDecimal.ZERO);

        for (SSSViewReportRowDTO row : rows) {
            totals.setOpening(totals.getOpening() + nullSafeInteger(row.getOpening()));
            totals.setOpeningValue(totals.getOpeningValue().add(nullSafeRate(row.getOpeningValue())));

            totals.setReceipt(totals.getReceipt() + nullSafeInteger(row.getReceipt()));

            totals.setPrimarySale(totals.getPrimarySale() + nullSafeInteger(row.getPrimarySale()));
            totals.setPrimaryValue(totals.getPrimaryValue().add(nullSafeRate(row.getPrimaryValue())));

            totals.setScheme(totals.getScheme() + nullSafeInteger(row.getScheme()));
            totals.setTotal(totals.getTotal() + nullSafeInteger(row.getTotal()));

            totals.setSalesReturn(totals.getSalesReturn() + nullSafeInteger(row.getSalesReturn()));
            totals.setReturnValue(totals.getReturnValue().add(nullSafeRate(row.getReturnValue())));

            totals.setClosing(totals.getClosing() + nullSafeInteger(row.getClosing()));
            totals.setClosingValue(totals.getClosingValue().add(nullSafeRate(row.getClosingValue())));

            totals.setExpiry(totals.getExpiry() + nullSafeInteger(row.getExpiry()));
            totals.setExpiryValue(totals.getExpiryValue().add(nullSafeRate(row.getExpiryValue())));

            totals.setBreakage(totals.getBreakage() + nullSafeInteger(row.getBreakage()));
            totals.setBreakageValue(totals.getBreakageValue().add(nullSafeRate(row.getBreakageValue())));

            totals.setBatchRecall(totals.getBatchRecall() + nullSafeInteger(row.getBatchRecall()));
            totals.setBatchRecallValue(totals.getBatchRecallValue().add(nullSafeRate(row.getBatchRecallValue())));

            totals.setSecondarySale(totals.getSecondarySale() + nullSafeInteger(row.getSecondarySale()));
            totals.setSecondarySaleValue(totals.getSecondarySaleValue().add(nullSafeRate(row.getSecondarySaleValue())));
        }

        return totals;
    }

    private void validateFilter(SSSViewFilterDTO dto) {
        if (dto.getMonth() == null || dto.getMonth() < 1 || dto.getMonth() > 12) {
            throw new RuntimeException("Month must be between 1 and 12.");
        }
        if (dto.getYear() == null || dto.getYear() < 2000 || dto.getYear() > 2100) {
            throw new RuntimeException("Year is invalid.");
        }
    }

    private boolean isEmpty(List<?> values) {
        return values == null || values.isEmpty();
    }

    private List<Integer> integerIdsOrDummy(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of(-1);
        }
        return ids;
    }

    private List<Long> longIdsOrDummy(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of(-1L);
        }
        return ids;
    }

    private Integer nullSafeInteger(Integer value) {
        return value != null ? value : 0;
    }

    private BigDecimal nullSafeRate(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private BigDecimal multiply(BigDecimal rate, Integer qty) {
        return nullSafeRate(rate).multiply(BigDecimal.valueOf(nullSafeInteger(qty).longValue()));
    }

    private String safeBatchNumber(String value) {
        if (value == null || value.isBlank()) {
            return "-";
        }
        return value.trim();
    }

    private String safeText(String value) {
        return value != null ? value : "";
    }
}
