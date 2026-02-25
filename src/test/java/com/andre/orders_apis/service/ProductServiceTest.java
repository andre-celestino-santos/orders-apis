package com.andre.orders_apis.service;

import com.andre.orders_apis.entity.Category;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
        savedProductMock.setId(1L);
        savedProductMock.setPublicId(UUID.randomUUID());

        Mockito.when(productRepository.save(Mockito.any())).thenReturn(savedProductMock);

        Product savedProduct = productService.create(new Product());

        Mockito.verify(productRepository, Mockito.atMostOnce()).save(Mockito.any());

        Assertions.assertThat(savedProduct.getId()).isEqualTo(1);
        Assertions.assertThat(savedProduct.getPublicId()).isEqualTo(savedProductMock.getPublicId());
    }

    @Test
    public void shouldUpdateDescriptionSuccessfully() {
        Product product = new Product();
        product.setId(1L);
        product.setPublicId(UUID.randomUUID());
        product.setDescription("Samsung Galaxy A07 128gb, 4gb");

        Product savedProductMock = new Product();
        savedProductMock.setId(1L);
        savedProductMock.setPublicId(product.getPublicId());
        savedProductMock.setDescription("new description");

        Mockito.when(productRepository.findByPublicIdAndActiveTrue(Mockito.any())).thenReturn(Optional.of(product));

        Mockito.when(productRepository.save(Mockito.any())).thenReturn(savedProductMock);

        Product savedProduct = productService.update(new Product());

        Mockito.verify(productRepository, Mockito.atMostOnce()).findByPublicIdAndActiveTrue(Mockito.any());
        Mockito.verify(productRepository, Mockito.atMostOnce()).save(Mockito.any());

        Assertions.assertThat(savedProduct.getId()).isEqualTo(1);
        Assertions.assertThat(savedProduct.getPublicId()).isEqualTo(product.getPublicId());
        Assertions.assertThat(savedProduct.getDescription()).isEqualTo("new description");
    }

    @Test
    public void shouldReturnExceptionWhenUpdateProductNotFound() {
        Product product = new Product();
        product.setPublicId(UUID.randomUUID());

        product.setDescription("Samsung Galaxy A07 128gb, 4gb");

        Mockito.when(productRepository.findByPublicIdAndActiveTrue(Mockito.any())).thenReturn(Optional.empty());

        ResourceNotFoundException resourceNotFoundException = Assertions.catchThrowableOfType(ResourceNotFoundException.class,
                () -> productService.update(product));

        Mockito.verify(productRepository, Mockito.atMostOnce()).findByPublicIdAndActiveTrue(Mockito.any());

        Assertions.assertThat(resourceNotFoundException.getCode())
                .isEqualTo(OrderApiError.PRODUCT_NOT_FOUND.getCode());
        Assertions.assertThat(resourceNotFoundException.getFormattedMessage())
                .isEqualTo(OrderApiError.PRODUCT_NOT_FOUND.getMessage().formatted(product.getPublicId()));
    }

    @Test
    public void shouldDeleteProductSuccessfully() {
        Product product = new Product();
        product.setId(1L);
        product.setPublicId(UUID.randomUUID());

        Assertions.assertThat(product.getActive()).isTrue();

        Mockito.when(productRepository.findByPublicIdAndActiveTrue(Mockito.any())).thenReturn(Optional.of(product));

        productService.delete(product.getPublicId());

        Mockito.verify(productRepository, Mockito.atMostOnce()).findByPublicIdAndActiveTrue(Mockito.any());
        Mockito.verify(productRepository, Mockito.atMostOnce()).save(productCaptor.capture());

        Product productCaptorValue = productCaptor.getValue();

        Assertions.assertThat(productCaptorValue.getId()).isEqualTo(1);
        Assertions.assertThat(productCaptorValue.getActive()).isFalse();
    }

    @Test
    public void shouldReturnExceptionWhenDeleteProductNotFound() {
        final UUID publicId = UUID.randomUUID();

        Mockito.when(productRepository.findByPublicIdAndActiveTrue(Mockito.any())).thenReturn(Optional.empty());

        ResourceNotFoundException resourceNotFoundException = Assertions.catchThrowableOfType(ResourceNotFoundException.class,
                () -> productService.delete(publicId));

        Mockito.verify(productRepository, Mockito.atMostOnce()).findByPublicIdAndActiveTrue(Mockito.any());

        Assertions.assertThat(resourceNotFoundException.getCode())
                .isEqualTo(OrderApiError.PRODUCT_NOT_FOUND.getCode());
        Assertions.assertThat(resourceNotFoundException.getFormattedMessage())
                .isEqualTo(OrderApiError.PRODUCT_NOT_FOUND.getMessage().formatted(publicId));
    }

    @Test
    public void shouldReturnAllActiveProductByCategoryWithPagination() {
        Product product = new Product();
        product.setId(1L);
        product.setPublicId(UUID.randomUUID());
        product.setBrand("Apple");
        product.setModel("Iphone 16");
        product.setCategory(Category.SMARTPHONE);

        Page<Product> pageSmartphoneProductMock = new PageImpl<>(List.of(product));

        Mockito.when(productRepository.findAllByCategoryAndActiveTrue(Mockito.any(), Mockito.any())).thenReturn(pageSmartphoneProductMock);

        Page<Product> pageSmartphoneProduct = productService.getAllByCategory(Category.SMARTPHONE, Pageable.ofSize(5));

        Assertions.assertThat(pageSmartphoneProduct.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(pageSmartphoneProduct.getContent()).hasSize(1);
    }

}