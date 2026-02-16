package com.andre.orders_apis.service;

import com.andre.orders_apis.entity.Product;
import com.andre.orders_apis.enums.OrderApiError;
import com.andre.orders_apis.exception.ResourceNotFoundException;
import com.andre.orders_apis.repository.ProductRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Captor
    private ArgumentCaptor<Product> productCaptor;

    @Test
    public void shouldCreateProductSuccessfully() {
        Product savedProductMock = new Product();
        savedProductMock.setId(1);

        Mockito.when(productRepository.save(Mockito.any())).thenReturn(savedProductMock);

        Product savedProduct = productService.create(new Product());

        Mockito.verify(productRepository, Mockito.atMostOnce()).save(Mockito.any());

        Assertions.assertThat(savedProduct.getId()).isEqualTo(1);
    }

    @Test
    public void shouldUpdateDescriptionSuccessfully() {
        Product product = new Product();
        product.setId(1);
        product.setDescription("Samsung Galaxy A07 128gb, 4gb");

        Product savedProductMock = new Product();
        savedProductMock.setId(1);
        savedProductMock.setDescription("new description");

        Mockito.when(productRepository.findByIdAndActiveTrue(Mockito.any())).thenReturn(Optional.of(product));

        Mockito.when(productRepository.save(Mockito.any())).thenReturn(savedProductMock);

        Product savedProduct = productService.update(new Product());

        Mockito.verify(productRepository, Mockito.atMostOnce()).findByIdAndActiveTrue(Mockito.any());
        Mockito.verify(productRepository, Mockito.atMostOnce()).save(Mockito.any());

        Assertions.assertThat(savedProduct.getId()).isEqualTo(1);
        Assertions.assertThat(savedProduct.getDescription()).isEqualTo("new description");
    }

    @Test
    public void shouldReturnExceptionWhenUpdateProductNotFound() {
        Product product = new Product();
        product.setId(1);
        product.setDescription("Samsung Galaxy A07 128gb, 4gb");

        Mockito.when(productRepository.findByIdAndActiveTrue(Mockito.any())).thenReturn(Optional.empty());

        ResourceNotFoundException resourceNotFoundException = Assertions.catchThrowableOfType(ResourceNotFoundException.class,
                () -> productService.update(product));

        Mockito.verify(productRepository, Mockito.atMostOnce()).findByIdAndActiveTrue(Mockito.any());

        Assertions.assertThat(resourceNotFoundException.getCode())
                .isEqualTo(OrderApiError.PRODUCT_NOT_FOUND.getCode());
        Assertions.assertThat(resourceNotFoundException.getFormattedMessage())
                .isEqualTo(OrderApiError.PRODUCT_NOT_FOUND.getMessage().formatted(product.getId()));
    }

    @Test
    public void shouldDeleteProductSuccessfully() {
        Product product = new Product();
        product.setId(1);

        Assertions.assertThat(product.getActive()).isTrue();

        Mockito.when(productRepository.findByIdAndActiveTrue(Mockito.any())).thenReturn(Optional.of(product));

        productService.delete(product.getId());

        Mockito.verify(productRepository, Mockito.atMostOnce()).findByIdAndActiveTrue(Mockito.any());
        Mockito.verify(productRepository, Mockito.atMostOnce()).save(productCaptor.capture());

        Product productCaptorValue = productCaptor.getValue();

        Assertions.assertThat(productCaptorValue.getId()).isEqualTo(1);
        Assertions.assertThat(productCaptorValue.getActive()).isFalse();
    }

    @Test
    public void shouldReturnExceptionWhenDeleteProductNotFound() {
        final Integer id = 999;

        Mockito.when(productRepository.findByIdAndActiveTrue(Mockito.any())).thenReturn(Optional.empty());

        ResourceNotFoundException resourceNotFoundException = Assertions.catchThrowableOfType(ResourceNotFoundException.class,
                () -> productService.delete(id));

        Mockito.verify(productRepository, Mockito.atMostOnce()).findByIdAndActiveTrue(Mockito.any());

        Assertions.assertThat(resourceNotFoundException.getCode())
                .isEqualTo(OrderApiError.PRODUCT_NOT_FOUND.getCode());
        Assertions.assertThat(resourceNotFoundException.getFormattedMessage())
                .isEqualTo(OrderApiError.PRODUCT_NOT_FOUND.getMessage().formatted(id));
    }

}