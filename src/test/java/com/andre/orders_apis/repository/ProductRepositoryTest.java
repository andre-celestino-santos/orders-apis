package com.andre.orders_apis.repository;

import com.andre.orders_apis.entity.Category;
import com.andre.orders_apis.entity.Product;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Optional;

@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void shouldCreateProductSuccessfully() {
        createProduct();
    }

    @Test
    public void shouldUpdateProductSuccessfully() {
        Product savedProduct = createProduct();

        savedProduct.setDescription("new description");

        savedProduct = productRepository.save(savedProduct);

        Assertions.assertThat(savedProduct.getDescription()).isEqualTo("new description");
    }

    @Test
    public void shouldReturnActiveProductSuccessfully() {
        Product savedProduct = createProduct();

        Optional<Product> activeProduct = productRepository.findByIdAndActiveTrue(savedProduct.getId());
        Assertions.assertThat(activeProduct).isPresent();
    }

    @Test
    public void shouldDeleteProductSuccessfully() {
        Product savedProduct = createProduct();

        savedProduct.setActive(false);

        savedProduct = productRepository.save(savedProduct);

        Assertions.assertThat(savedProduct.getActive()).isFalse();
    }

    @Test
    public void shouldReturnAllActiveProductByCategoryWithPagination() {
        long smartphoneProductTotal = 20;
        long tabletProductTotal = 10;

        for (int i = 0; i < smartphoneProductTotal; i++) {
            createProduct();
        }

        for (int i = 0; i < tabletProductTotal; i++) {
            createProduct(Category.TABLET, "Lenovo", "Tab", null);
        }

        for (int i = 0; i < 5; i++) {
            createProduct(Category.TABLET, "Lenovo", "Tab", false);
        }

        int pageSize = 5;
        Pageable pageable = Pageable.ofSize(pageSize);

        Page<Product> pageSmartphoneProduct = productRepository.findAllByCategoryAndActiveTrue(Category.SMARTPHONE, pageable);

        Assertions.assertThat(pageSmartphoneProduct.getTotalElements()).isEqualTo(smartphoneProductTotal);
        Assertions.assertThat(pageSmartphoneProduct.getTotalPages()).isEqualTo(smartphoneProductTotal / pageSize);

        for (Product product : pageSmartphoneProduct.getContent()) {
            Assertions.assertThat(product.getId()).isGreaterThan(0);
            Assertions.assertThat(product.getBrand()).isEqualTo("Samsung");
            Assertions.assertThat(product.getModel()).isEqualTo("A07");
            Assertions.assertThat(product.getDescription()).isEqualTo("Samsung A07");
        }

        Page<Product> pageTabletProduct = productRepository.findAllByCategoryAndActiveTrue(Category.TABLET, pageable);

        Assertions.assertThat(pageTabletProduct.getTotalElements()).isEqualTo(tabletProductTotal);
        Assertions.assertThat(pageTabletProduct.getTotalPages()).isEqualTo(tabletProductTotal / pageSize);

        for (Product product : pageTabletProduct.getContent()) {
            Assertions.assertThat(product.getId()).isGreaterThan(0);
            Assertions.assertThat(product.getBrand()).isEqualTo("Lenovo");
            Assertions.assertThat(product.getModel()).isEqualTo("Tab");
            Assertions.assertThat(product.getDescription()).isEqualTo("Lenovo Tab");
        }
    }

    private Product createProduct(Category category, String brand, String model, Boolean active) {
        Product product = new Product();
        product.setBrand(brand);
        product.setModel(model);
        product.setPrice(new BigDecimal("594.00"));
        product.setCategory(category);
        product.setStockQuantity(5);
        product.setDescription(brand + " " + model);
        if (active != null) {
            product.setActive(active);
        }

        Product savedProduct = productRepository.save(product);

        Assertions.assertThat(savedProduct.getId()).isGreaterThan(0);
        Assertions.assertThat(savedProduct.getBrand()).isEqualTo(product.getBrand());
        Assertions.assertThat(savedProduct.getModel()).isEqualTo(product.getModel());
        Assertions.assertThat(savedProduct.getPrice()).isEqualByComparingTo(product.getPrice());
        Assertions.assertThat(savedProduct.getCategory()).isEqualTo(product.getCategory());
        Assertions.assertThat(savedProduct.getStockQuantity()).isEqualTo(product.getStockQuantity());
        Assertions.assertThat(savedProduct.getDescription()).isEqualTo(product.getDescription());
        if (active != null) {
            Assertions.assertThat(savedProduct.getActive()).isEqualTo(active);
        } else {
            Assertions.assertThat(savedProduct.getActive()).isTrue();
        }

        Assertions.assertThat(savedProduct.getCreatedAt()).isNotNull();
        Assertions.assertThat(savedProduct.getUpdatedAt()).isNotNull();

        return savedProduct;
    }

    private Product createProduct() {
        return createProduct(Category.SMARTPHONE, "Samsung", "A07", null);
    }

}