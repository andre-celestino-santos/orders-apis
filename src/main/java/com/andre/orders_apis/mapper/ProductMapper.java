package com.andre.orders_apis.mapper;

import com.andre.orders_apis.dto.ProductRequestDto;
import com.andre.orders_apis.dto.ProductResponseDto;
import com.andre.orders_apis.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(Integer id, ProductRequestDto productRequestDto){
        Product product = new Product();
        product.setId(id);
        product.setBrand(productRequestDto.getBrand());
        product.setModel(productRequestDto.getModel());
        product.setPrice(productRequestDto.getPrice());
        product.setCategory(productRequestDto.getCategory());
        product.setStockQuantity(productRequestDto.getStockQuantity());
        product.setDescription(productRequestDto.getDescription());
        return product;
    }

    public Product toEntity(ProductRequestDto productRequestDto) {
        return toEntity(null, productRequestDto);
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
        productResponseDto.setCreatedAt(product.getCreatedAt());
        productResponseDto.setUpdatedAt(product.getUpdatedAt());
        return productResponseDto;
    }

    public Page<ProductResponseDto> toResponse(Page<Product> pageProduct) {
        return pageProduct.map(this::toResponse);
    }

}