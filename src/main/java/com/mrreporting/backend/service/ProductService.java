package com.mrreporting.backend.service;

import com.mrreporting.backend.dto.ProductRequestDTO;
import com.mrreporting.backend.entity.Product;
import com.mrreporting.backend.entity.State;
import com.mrreporting.backend.repository.ProductRepository;
import com.mrreporting.backend.repository.StateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StateRepository stateRepository;

    @Transactional
    public Product createProduct(ProductRequestDTO dto) {
        Product product = new Product();

        // Map basic info
        product.setProductName(dto.getProductName());
        product.setProductCode(dto.getProductCode());
        product.setProductType(dto.getProductType());
        product.setProductShortName(dto.getProductShortName());
        product.setPackageSize(dto.getPackageSize());
        product.setSamplePackageSize(dto.getSamplePackageSize());

        // Map rates
        product.setPtw(dto.getPtw());
        product.setPtr(dto.getPtr());
        product.setMrp(dto.getMrp());
        product.setSampleRate(dto.getSampleRate());
        product.setIncludeVat(dto.getIncludeVat());

        // 🔗 Handle Many-to-Many mapping
        if (dto.getStateIds() != null && !dto.getStateIds().isEmpty()) {
            List<State> selectedStates = stateRepository.findAllById(dto.getStateIds());
            product.setStates(new HashSet<>(selectedStates));
        }

        return productRepository.save(product);
    }
}