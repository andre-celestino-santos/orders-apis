package com.andre.orders_apis.repository;

import com.andre.orders_apis.entity.Category;
import com.andre.orders_apis.entity.Product;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void shouldCreateProductSuccessfully() {
        Product product = new Product();
        product.setBrand("Samsung");
        product.setModel("A07");
        product.setPrice(new BigDecimal("594.00"));
        product.setCategory(Category.SMARTPHONE);
        product.setStockQuantity(5);
        product.setDescription("Samsung Galaxy A07 128gb, 4gb");

        Product savedProduct = productRepository.save(product);

        Assertions.assertThat(savedProduct.getId()).isGreaterThan(0);
        Assertions.assertThat(savedProduct.getBrand()).isEqualTo(product.getBrand());
        Assertions.assertThat(savedProduct.getModel()).isEqualTo(product.getModel());
        Assertions.assertThat(savedProduct.getPrice()).isEqualByComparingTo(product.getPrice());
        Assertions.assertThat(savedProduct.getCategory()).isEqualTo(product.getCategory());
        Assertions.assertThat(savedProduct.getStockQuantity()).isEqualTo(product.getStockQuantity());
        Assertions.assertThat(savedProduct.getDescription()).isEqualTo(product.getDescription());
        Assertions.assertThat(savedProduct.getActive()).isTrue();
        Assertions.assertThat(savedProduct.getCreatedAt()).isNotNull();
        Assertions.assertThat(savedProduct.getUpdatedAt()).isNotNull();
    }

    @Test
    public void shouldUpdateProductSuccessfully() {
        Product product = new Product();
        product.setBrand("Samsung");
        product.setModel("A07");
        product.setPrice(new BigDecimal("594.00"));
        product.setCategory(Category.SMARTPHONE);
        product.setStockQuantity(5);
        product.setDescription("Samsung Galaxy A07 128gb, 4gb");

        Product savedProduct = productRepository.save(product);

        Assertions.assertThat(savedProduct.getId()).isGreaterThan(0);
        Assertions.assertThat(savedProduct.getBrand()).isEqualTo(product.getBrand());
        Assertions.assertThat(savedProduct.getModel()).isEqualTo(product.getModel());
        Assertions.assertThat(savedProduct.getPrice()).isEqualByComparingTo(product.getPrice());
        Assertions.assertThat(savedProduct.getCategory()).isEqualTo(product.getCategory());
        Assertions.assertThat(savedProduct.getStockQuantity()).isEqualTo(product.getStockQuantity());
        Assertions.assertThat(savedProduct.getDescription()).isEqualTo(product.getDescription());
        Assertions.assertThat(savedProduct.getActive()).isTrue();
        Assertions.assertThat(savedProduct.getCreatedAt()).isNotNull();
        Assertions.assertThat(savedProduct.getUpdatedAt()).isNotNull();

        savedProduct.setDescription("new description");

        savedProduct = productRepository.save(savedProduct);

        Assertions.assertThat(savedProduct.getDescription()).isEqualTo("new description");
    }

    @Test
    public void shouldReturnActiveProductSuccessfully() {
        Product product = new Product();
        product.setBrand("Samsung");
        product.setModel("A07");
        product.setPrice(new BigDecimal("594.00"));
        product.setCategory(Category.SMARTPHONE);
        product.setStockQuantity(5);
        product.setDescription("Samsung Galaxy A07 128gb, 4gb");

        Product savedProduct = productRepository.save(product);

        Assertions.assertThat(savedProduct.getId()).isGreaterThan(0);
        Assertions.assertThat(savedProduct.getActive()).isTrue();

        Optional<Product> activeProduct = productRepository.findByIdAndActiveTrue(savedProduct.getId());
        Assertions.assertThat(activeProduct).isPresent();
    }

}