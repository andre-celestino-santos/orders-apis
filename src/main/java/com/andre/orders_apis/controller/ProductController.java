package com.andre.orders_apis.controller;

import com.andre.orders_apis.dto.ProductRequestDto;
import com.andre.orders_apis.dto.ProductResponseDto;
import com.andre.orders_apis.entity.Product;
import com.andre.orders_apis.mapper.ProductMapper;
import com.andre.orders_apis.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    @PostMapping
    public ResponseEntity<ProductResponseDto> create(@RequestBody @Valid ProductRequestDto body) {
        Product productRequest = productMapper.toEntity(body);

        Product product = productService.create(productRequest);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(product.getId())
                .toUri();

        ProductResponseDto response = productMapper.toResponse(product);

        return ResponseEntity.created(location).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponseDto> update(@PathVariable Integer id,
                                                     @RequestBody ProductRequestDto body) {
        Product productRequest = productMapper.toEntity(id, body);

        Product product = productService.update(productRequest);

        ProductResponseDto response = productMapper.toResponse(product);

        return ResponseEntity.ok(response);
    }

}