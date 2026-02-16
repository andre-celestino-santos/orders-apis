package com.andre.orders_apis.service;

import com.andre.orders_apis.entity.Product;
import com.andre.orders_apis.enums.OrderApiError;
import com.andre.orders_apis.exception.ResourceNotFoundException;
import com.andre.orders_apis.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public Product create(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public Product update(Product product) {
        Optional<Product> productOpt = productRepository.findByIdAndActiveTrue(product.getId());

        if (productOpt.isEmpty()) {
            throw new ResourceNotFoundException(OrderApiError.PRODUCT_NOT_FOUND, product.getId());
        }

        Product productUpdate = productOpt.get();

        productUpdate.setDescription(Optional.ofNullable(product.getDescription()).orElse(productUpdate.getDescription()));

        return productRepository.save(productUpdate);
    }

}