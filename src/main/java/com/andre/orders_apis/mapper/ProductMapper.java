package com.andre.orders_apis.mapper;

import com.andre.orders_apis.dto.ProductRequestDto;
import com.andre.orders_apis.dto.ProductResponseDto;
import com.andre.orders_apis.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(ProductRequestDto productRequestDto){
        Product product = new Product();
        product.setBrand(productRequestDto.getBrand());
        product.setModel(productRequestDto.getModel());
        product.setPrice(productRequestDto.getPrice());
        product.setCategory(productRequestDto.getCategory());
        product.setStockQuantity(productRequestDto.getStockQuantity());
        product.setDescription(productRequestDto.getDescription());
        product.setActive(productRequestDto.getActive());
        return product;
    }

    public ProductResponseDto toResponse(Product product) {
        ProductResponseDto productResponseDto = new ProductResponseDto();
        productResponseDto.setId(product.getId());
        productResponseDto.setBrand(product.getBrand());
        productResponseDto.setModel(product.getModel());
        productResponseDto.setPrice(product.getPrice());
        productResponseDto.setCategory(product.getCategory());
        productResponseDto.setStockQuantity(product.getStockQuantity());
        productResponseDto.setDescription(product.getDescription());
        productResponseDto.setActive(product.getActive());
        productResponseDto.setCreatedAt(product.getCreatedAt());
        productResponseDto.setUpdatedAt(product.getUpdatedAt());
        return productResponseDto;
    }

}