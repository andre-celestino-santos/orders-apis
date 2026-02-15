package com.andre.orders_apis.service;

import com.andre.orders_apis.entity.Product;
import com.andre.orders_apis.repository.ProductRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Test
    public void shouldCreateProductSuccessfully() {
        Product savedProductMock = new Product();
        savedProductMock.setId(1);

        Mockito.when(productRepository.save(Mockito.any())).thenReturn(savedProductMock);

        Product savedProduct = productService.create(new Product());

        Mockito.verify(productRepository, Mockito.atLeastOnce()).save(Mockito.any());

        Assertions.assertThat(savedProduct.getId()).isEqualTo(1);
    }

}