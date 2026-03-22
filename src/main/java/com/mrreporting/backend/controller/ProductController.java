package com.mrreporting.backend.controller;

import com.mrreporting.backend.dto.ProductRequestDTO;
import com.mrreporting.backend.entity.Product;
import com.mrreporting.backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/masters/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createProduct(@RequestBody ProductRequestDTO dto) {
        try {
            Product savedProduct = productService.createProduct(dto);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Product created successfully!");
            response.put("data", savedProduct);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error creating product: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}