package com.andre.orders_apis.mapper;

import com.andre.orders_apis.dto.ProductRequestDto;
import com.andre.orders_apis.dto.ProductResponseDto;
import com.andre.orders_apis.entity.Category;
import com.andre.orders_apis.entity.Product;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductMapperTest {

    private final ProductMapper productMapper = new ProductMapper();

    @Test
    public void shouldReturnEntitySuccessfully() {
        ProductRequestDto request = new ProductRequestDto();
        request.setBrand("Samsung");
        request.setModel("A07");
        request.setPrice(new BigDecimal("594.00"));
        request.setCategory(Category.SMARTPHONE);
        request.setStockQuantity(5);
        request.setDescription("Samsung Galaxy A07 128gb, 4gb");

        Product entity = productMapper.toEntity(request);

        Assertions.assertThat(entity.getId()).isNull();
        Assertions.assertThat(entity.getBrand()).isEqualTo(request.getBrand());
        Assertions.assertThat(entity.getModel()).isEqualTo(request.getModel());
        Assertions.assertThat(entity.getPrice()).isEqualByComparingTo(request.getPrice());
        Assertions.assertThat(entity.getCategory()).isEqualTo(request.getCategory());
        Assertions.assertThat(entity.getStockQuantity()).isEqualTo(request.getStockQuantity());
        Assertions.assertThat(entity.getDescription()).isEqualTo(request.getDescription());
        Assertions.assertThat(entity.getActive()).isTrue();
        Assertions.assertThat(entity.getCreatedAt()).isNull();
        Assertions.assertThat(entity.getUpdatedAt()).isNull();
    }

    @Test
    public void shouldReturnResponseSuccessfully() {
        Product product = new Product();
        product.setId(1);
        product.setBrand("Samsung");
        product.setModel("A07");
        product.setPrice(new BigDecimal("594.00"));
        product.setCategory(Category.SMARTPHONE);
        product.setStockQuantity(5);
        product.setDescription("Samsung Galaxy A07 128gb, 4gb");
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        ProductResponseDto response = productMapper.toResponse(product);

        Assertions.assertThat(response.getId()).isEqualTo(product.getId());
        Assertions.assertThat(response.getBrand()).isEqualTo(product.getBrand());
        Assertions.assertThat(response.getModel()).isEqualTo(product.getModel());
        Assertions.assertThat(response.getPrice()).isEqualByComparingTo(product.getPrice());
        Assertions.assertThat(response.getCategory()).isEqualTo(product.getCategory());
        Assertions.assertThat(response.getStockQuantity()).isEqualTo(product.getStockQuantity());
        Assertions.assertThat(response.getDescription()).isEqualTo(product.getDescription());
        Assertions.assertThat(response.getCreatedAt()).isNotNull();
        Assertions.assertThat(response.getUpdatedAt()).isNotNull();
    }

}