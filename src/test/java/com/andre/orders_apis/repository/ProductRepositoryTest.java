package com.andre.orders_apis.repository;

import com.andre.orders_apis.entity.Categoty;
import com.andre.orders_apis.entity.Product;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void createNewProduct() {
        Product product = new Product();
        product.setBrand("Samsung");
        product.setModel("A07");
        product.setPrice(new BigDecimal("594.00"));
        product.setCategoty(Categoty.SMARTPHONE);
        product.setStockQuantity(5);
        product.setDescription("Samsung Galaxy A07 128gb, 4gb");
        product.setActive(true);

        Product savedProduct = productRepository.save(product);

        Assertions.assertThat(savedProduct.getId()).isGreaterThan(0);
        Assertions.assertThat(savedProduct.getBrand()).isEqualTo(product.getBrand());
        Assertions.assertThat(savedProduct.getModel()).isEqualTo(product.getModel());
        Assertions.assertThat(savedProduct.getPrice()).isEqualByComparingTo(product.getPrice());
        Assertions.assertThat(savedProduct.getCategoty()).isEqualTo(product.getCategoty());
        Assertions.assertThat(savedProduct.getStockQuantity()).isEqualTo(product.getStockQuantity());
        Assertions.assertThat(savedProduct.getDescription()).isEqualTo(product.getDescription());
        Assertions.assertThat(savedProduct.getActive()).isEqualTo(product.getActive());
        Assertions.assertThat(savedProduct.getCreatedAt()).isNotNull();
        Assertions.assertThat(savedProduct.getUpdatedAt()).isNotNull();
    }

    @Test
    public void updateProduct() {
        Product product = new Product();
        product.setBrand("Samsung");
        product.setModel("A07");
        product.setPrice(new BigDecimal("594.00"));
        product.setCategoty(Categoty.SMARTPHONE);
        product.setStockQuantity(5);
        product.setDescription("Samsung Galaxy A07 128gb, 4gb");
        product.setActive(true);

        Product savedProduct = productRepository.save(product);

        Assertions.assertThat(savedProduct.getId()).isGreaterThan(0);
        Assertions.assertThat(savedProduct.getBrand()).isEqualTo(product.getBrand());
        Assertions.assertThat(savedProduct.getModel()).isEqualTo(product.getModel());
        Assertions.assertThat(savedProduct.getPrice()).isEqualByComparingTo(product.getPrice());
        Assertions.assertThat(savedProduct.getCategoty()).isEqualTo(product.getCategoty());
        Assertions.assertThat(savedProduct.getStockQuantity()).isEqualTo(product.getStockQuantity());
        Assertions.assertThat(savedProduct.getDescription()).isEqualTo(product.getDescription());
        Assertions.assertThat(savedProduct.getActive()).isEqualTo(product.getActive());
        Assertions.assertThat(savedProduct.getCreatedAt()).isNotNull();
        Assertions.assertThat(savedProduct.getUpdatedAt()).isNotNull();

        savedProduct.setPrice(new BigDecimal("510.92"));

        savedProduct = productRepository.save(savedProduct);

        Assertions.assertThat(savedProduct.getPrice()).isEqualByComparingTo(new BigDecimal("510.92"));
    }

}