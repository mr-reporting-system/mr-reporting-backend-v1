package com.mrreporting.backend.service;

import com.mrreporting.backend.dto.*;
import com.mrreporting.backend.entity.Product;
import com.mrreporting.backend.entity.Provider;
import com.mrreporting.backend.entity.StockSalesStatement;
import com.mrreporting.backend.entity.StockSalesStatementItem;
import com.mrreporting.backend.repository.ProductRepository;
import com.mrreporting.backend.repository.ProviderRepository;
import com.mrreporting.backend.repository.StockSalesStatementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StockSalesStatementService {

    @Autowired
    private StockSalesStatementRepository stockSalesStatementRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<StockistOptionDTO> getStockistsByEmployee(Long employeeId, Integer stateId, Integer districtId) {
        List<Provider> providers;

        if (stateId != null && districtId != null) {
            providers = providerRepository
                    .findByEmployeeIdAndStateIdAndDistrictIdAndIsActiveTrueOrderByProviderNameAsc(
                            employeeId,
                            stateId,
                            districtId
                    );
        } else {
            providers = providerRepository.findByEmployeeIdAndIsActiveTrueOrderByProviderNameAsc(employeeId);
        }

        return providers.stream()
                .map(provider -> new StockistOptionDTO(
                        provider.getId(),
                        provider.getProviderName(),
                        provider.getType()
                ))
                .toList();
    }



    // fetch the SSS form for a stockist + month + year.
    // if records already exist, return saved values for modify mode.
    // otherwise return empty rows populated from the products mapped to the stockist's state.
    @Transactional(readOnly = true)
    public StockSalesStatementFormResponseDTO getStatementForm(
            Long providerId,
            Integer stateId,
            Integer month,
            Integer year
    ) {
        validateMonthAndYear(month, year);

        Provider provider = providerRepository.findByIdAndIsActiveTrue(providerId)
                .orElseThrow(() -> new RuntimeException("Active provider not found with id: " + providerId));

        Integer resolvedStateId = stateId != null ? stateId : provider.getState().getId();

        Optional<StockSalesStatement> existingOpt =
                stockSalesStatementRepository.findByStockistIdAndMonthAndYear(providerId, month, year);

        if (existingOpt.isPresent()) {
            StockSalesStatement existing = existingOpt.get();

            if (stateId != null && !stateId.equals(existing.getState().getId())) {
                throw new RuntimeException("Selected state does not match the saved statement state.");
            }

            List<Product> mappedProducts =
                    productRepository.findDistinctByStatesIdOrderByProductNameAsc(existing.getState().getId());

            Map<Long, StockSalesStatementItem> savedItemsByProductId = existing.getItems()
                    .stream()
                    .collect(Collectors.toMap(
                            item -> item.getProduct().getId(),
                            item -> item,
                            (left, right) -> left,
                            LinkedHashMap::new
                    ));

            List<StockSalesStatementRowResponseDTO> rows = new ArrayList<>();

            for (Product product : mappedProducts) {
                StockSalesStatementItem savedItem = savedItemsByProductId.remove(product.getId());
                if (savedItem != null) {
                    rows.add(toRowResponse(savedItem));
                } else {
                    rows.add(toEmptyRow(product));
                }
            }

            savedItemsByProductId.values().stream()
                    .sorted(Comparator.comparing(
                            item -> safeText(item.getProduct().getProductName()),
                            String.CASE_INSENSITIVE_ORDER
                    ))
                    .forEach(item -> rows.add(toRowResponse(item)));

            return toFormResponse(existing, rows, true);
        }

        if (!resolvedStateId.equals(provider.getState().getId())) {
            throw new RuntimeException("Selected provider does not belong to the selected state.");
        }

        List<Product> mappedProducts =
                productRepository.findDistinctByStatesIdOrderByProductNameAsc(resolvedStateId);

        List<StockSalesStatementRowResponseDTO> rows = mappedProducts.stream()
                .map(this::toEmptyRow)
                .toList();

        return new StockSalesStatementFormResponseDTO(
                null,
                provider.getState().getId(),
                provider.getState().getStateName(),
                provider.getDistrict().getId(),
                provider.getDistrict().getDistrictName(),
                provider.getEmployee().getId(),
                provider.getEmployee().getName(),
                provider.getId(),
                provider.getProviderName(),
                month,
                year,
                false,
                rows
        );
    }



    // upsert save for submit / modify.
    // if stockist + month + year already exists, the rows are replaced.
    @Transactional
    public StockSalesStatementFormResponseDTO saveStatement(StockSalesStatementRequestDTO dto) {
        Long providerId = dto.getResolvedProviderId();

        if (providerId == null) {
            throw new RuntimeException("Provider is required.");
        }
        validateMonthAndYear(dto.getMonth(), dto.getYear());

        if (dto.getRows() == null || dto.getRows().isEmpty()) {
            throw new RuntimeException("At least one product row is required.");
        }

        Provider provider = providerRepository.findByIdAndIsActiveTrue(providerId)
                .orElseThrow(() -> new RuntimeException("Active provider not found with id: " + providerId));

        Optional<StockSalesStatement> existingOpt =
                stockSalesStatementRepository.findByStockistIdAndMonthAndYear(
                        providerId,
                        dto.getMonth(),
                        dto.getYear()
                );

        Integer statementStateId = existingOpt
                .map(statement -> statement.getState().getId())
                .orElse(provider.getState().getId());

        Set<Long> allowedProductIds = productRepository.findDistinctByStatesIdOrderByProductNameAsc(statementStateId)
                .stream()
                .map(Product::getId)
                .collect(Collectors.toSet());

        Map<Long, BigDecimal> existingRatesByProductId = new HashMap<>();
        existingOpt.ifPresent(existing ->
                existing.getItems().forEach(item ->
                        existingRatesByProductId.put(item.getProduct().getId(), nullSafeRate(item.getNetRate()))
                )
        );

        List<Long> productIds = dto.getRows().stream()
                .map(StockSalesStatementRowRequestDTO::getProductId)
                .toList();

        if (productIds.stream().anyMatch(Objects::isNull)) {
            throw new RuntimeException("Each row must contain a productId.");
        }

        Set<Long> uniqueProductIds = new LinkedHashSet<>(productIds);
        if (uniqueProductIds.size() != productIds.size()) {
            throw new RuntimeException("Duplicate product rows are not allowed.");
        }

        List<Product> products = productRepository.findAllById(uniqueProductIds);
        if (products.size() != uniqueProductIds.size()) {
            throw new RuntimeException("One or more selected products were not found.");
        }

        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, product -> product));

        StockSalesStatement statement = existingOpt.orElseGet(StockSalesStatement::new);

        if (statement.getId() == null) {
            statement.setState(provider.getState());
            statement.setDistrict(provider.getDistrict());
            statement.setEmployee(provider.getEmployee());
            statement.setStockist(provider);
            statement.setMonth(dto.getMonth());
            statement.setYear(dto.getYear());
        }

        statement.getItems().clear();

        for (StockSalesStatementRowRequestDTO row : dto.getRows()) {
            Product product = productMap.get(row.getProductId());
            if (product == null) {
                throw new RuntimeException("Product not found with id: " + row.getProductId());
            }

            if (!allowedProductIds.contains(product.getId())) {
                throw new RuntimeException("Product " + product.getProductName() + " is not mapped to the selected state.");
            }

            StockSalesStatementItem item = new StockSalesStatementItem();
            item.setProduct(product);

            item.setNetRate(existingRatesByProductId.getOrDefault(product.getId(), defaultNetRate(product)));

            item.setOpening(nullSafeInteger(row.getOpening()));
            item.setReceipt(nullSafeInteger(row.getReceipt()));
            item.setPrimarySale(nullSafeInteger(row.getPrimarySale()));
            item.setScheme(nullSafeInteger(row.getScheme()));
            item.setSalesReturn(nullSafeInteger(row.getSalesReturn()));
            item.setClosing(nullSafeInteger(row.getClosing()));
            item.setExpiry(nullSafeInteger(row.getExpiry()));
            item.setBreakage(nullSafeInteger(row.getBreakage()));
            item.setBatchRecall(nullSafeInteger(row.getBatchRecall()));
            item.setBatchNumber(normalizeText(row.getBatchNumber()));
            item.setSecondarySale(nullSafeInteger(row.getSecondarySale()));

            statement.addItem(item);
        }

        StockSalesStatement saved = stockSalesStatementRepository.save(statement);

        List<StockSalesStatementRowResponseDTO> rows = saved.getItems().stream()
                .sorted(Comparator.comparing(
                        item -> safeText(item.getProduct().getProductName()),
                        String.CASE_INSENSITIVE_ORDER
                ))
                .map(this::toRowResponse)
                .toList();

        return toFormResponse(saved, rows, true);
    }


    private StockSalesStatementFormResponseDTO toFormResponse(
            StockSalesStatement statement,
            List<StockSalesStatementRowResponseDTO> rows,
            boolean existing
    ) {
        return new StockSalesStatementFormResponseDTO(
                statement.getId(),
                statement.getState().getId(),
                statement.getState().getStateName(),
                statement.getDistrict().getId(),
                statement.getDistrict().getDistrictName(),
                statement.getEmployee().getId(),
                statement.getEmployee().getName(),
                statement.getStockist().getId(),
                statement.getStockist().getProviderName(),
                statement.getMonth(),
                statement.getYear(),
                existing,
                rows
        );
    }

    private StockSalesStatementRowResponseDTO toEmptyRow(Product product) {
        return new StockSalesStatementRowResponseDTO(
                product.getId(),
                product.getProductCode(),
                product.getProductName(),
                defaultNetRate(product),
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                null,
                0
        );
    }

    private StockSalesStatementRowResponseDTO toRowResponse(StockSalesStatementItem item) {
        return new StockSalesStatementRowResponseDTO(
                item.getProduct().getId(),
                item.getProduct().getProductCode(),
                item.getProduct().getProductName(),
                nullSafeRate(item.getNetRate()),
                nullSafeInteger(item.getOpening()),
                nullSafeInteger(item.getReceipt()),
                nullSafeInteger(item.getPrimarySale()),
                nullSafeInteger(item.getScheme()),
                nullSafeInteger(item.getSalesReturn()),
                nullSafeInteger(item.getClosing()),
                nullSafeInteger(item.getExpiry()),
                nullSafeInteger(item.getBreakage()),
                nullSafeInteger(item.getBatchRecall()),
                item.getBatchNumber(),
                nullSafeInteger(item.getSecondarySale())
        );
    }

    private void validateMonthAndYear(Integer month, Integer year) {
        if (month == null || month < 1 || month > 12) {
            throw new RuntimeException("Month must be between 1 and 12.");
        }
        if (year == null || year < 2000 || year > 2100) {
            throw new RuntimeException("Year is invalid.");
        }
    }

    private Integer nullSafeInteger(Integer value) {
        return value != null ? value : 0;
    }

    private BigDecimal nullSafeRate(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private BigDecimal defaultNetRate(Product product) {
        return product.getPtr() != null ? product.getPtr() : BigDecimal.ZERO;
    }

    private String normalizeText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private String safeText(String value) {
        return value != null ? value : "";
    }
}
