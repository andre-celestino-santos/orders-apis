package com.andre.orders_apis.service;

import com.andre.orders_apis.entity.Order;
import com.andre.orders_apis.entity.OrderItem;
import com.andre.orders_apis.entity.Product;
import com.andre.orders_apis.enums.OrderApiError;
import com.andre.orders_apis.exception.BusinessException;
import com.andre.orders_apis.exception.ResourceNotFoundException;
import com.andre.orders_apis.repository.OrderItemRepository;
import com.andre.orders_apis.repository.OrderRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Captor
    private ArgumentCaptor<Product> productCaptor;

    @Captor
    private ArgumentCaptor<List<OrderItem>> itemsCaptor;

    @Test
    public void shouldCreateOrderSuccessfully() {
        Order order = new Order();
        order.setCustomerId("abc123");

        List<OrderItem> items = new ArrayList<>();
        OrderItem item = new OrderItem();
        item.setQuantity(10);
        Product product = new Product();
        product.setId(1L);
        item.setProduct(product);
        items.add(item);

        order.setItems(items);

        Mockito.when(orderRepository.save(Mockito.any())).thenReturn(new Order());

        Product savedProduct = new Product();
        savedProduct.setStockQuantity(12);

        Optional<Product> optProduct = Optional.of(savedProduct);

        Mockito.when(productRepository.findByIdAndActiveTrue(Mockito.any())).thenReturn(optProduct);

        Mockito.when(productRepository.save(productCaptor.capture())).thenReturn(product);

        List<OrderItem> savedItems = new ArrayList<>();

        Mockito.when(orderItemRepository.saveAll(itemsCaptor.capture())).thenReturn(savedItems);

        Order savedOrder = orderService.create(order);

        Assertions.assertThat(savedOrder).isNotNull();

        Mockito.verify(orderRepository, Mockito.atMostOnce()).save(Mockito.any());

        Mockito.verify(productRepository, Mockito.atMostOnce()).findByIdAndActiveTrue(Mockito.any());

        Mockito.verify(productRepository, Mockito.atMostOnce()).save(Mockito.any());

        Product productCaptorValue = productCaptor.getValue();

        Assertions.assertThat(productCaptorValue.getStockQuantity()).isEqualTo(2);

        Mockito.verify(orderItemRepository, Mockito.atMostOnce()).saveAll(Mockito.any());

        List<OrderItem> itemsCaptorValue = itemsCaptor.getValue();

        Assertions.assertThat(itemsCaptorValue).hasSize(1);

        OrderItem orderItemCaptorValue = itemsCaptorValue.get(0);

        Assertions.assertThat(orderItemCaptorValue.getOrder()).isNotNull();
        Assertions.assertThat(orderItemCaptorValue.getProduct()).isNotNull();
    }

    @Test
    public void shouldReturnExceptionWhenProductNotFound() {
        Order order = new Order();
        order.setCustomerId("abc123");

        List<OrderItem> items = new ArrayList<>();
        OrderItem item = new OrderItem();
        item.setQuantity(10);
        Product product = new Product();
        product.setId(12L);
        item.setProduct(product);
        items.add(item);

        order.setItems(items);

        Mockito.when(orderRepository.save(Mockito.any())).thenReturn(new Order());

        Mockito.when(productRepository.findByIdAndActiveTrue(Mockito.any())).thenReturn(Optional.empty());

        ResourceNotFoundException resourceNotFoundException = Assertions.catchThrowableOfType(ResourceNotFoundException.class,
                () -> orderService.create(order));

        Mockito.verify(orderRepository, Mockito.atMostOnce()).save(Mockito.any());

        Mockito.verify(productRepository, Mockito.atMostOnce()).findByIdAndActiveTrue(Mockito.any());

        Mockito.verify(productRepository, Mockito.never()).save(Mockito.any());

        Mockito.verify(orderItemRepository, Mockito.never()).saveAll(Mockito.any());

        Assertions.assertThat(resourceNotFoundException.getCode()).isEqualTo(OrderApiError.PRODUCT_NOT_FOUND.getCode());
        Assertions.assertThat(resourceNotFoundException.getFormattedMessage())
                .isEqualTo(OrderApiError.PRODUCT_NOT_FOUND.getMessage().formatted(product.getId()));
    }

    @Test
    public void shouldReturnExceptionWhenThereAreNoStockAvailable() {
        Order order = new Order();
        order.setCustomerId("abc123");

        List<OrderItem> items = new ArrayList<>();
        OrderItem item = new OrderItem();
        item.setQuantity(10);
        Product product = new Product();
        product.setId(1L);
        item.setProduct(product);
        items.add(item);

        order.setItems(items);

        Mockito.when(orderRepository.save(Mockito.any())).thenReturn(new Order());

        Product savedProduct = new Product();
        savedProduct.setStockQuantity(5);

        Optional<Product> optProduct = Optional.of(savedProduct);

        Mockito.when(productRepository.findByIdAndActiveTrue(Mockito.any())).thenReturn(optProduct);

        BusinessException businessException = Assertions.catchThrowableOfType(BusinessException.class, () -> orderService.create(order));

        Mockito.verify(orderRepository, Mockito.atMostOnce()).save(Mockito.any());

        Mockito.verify(productRepository, Mockito.atMostOnce()).findByIdAndActiveTrue(Mockito.any());

        Mockito.verify(productRepository, Mockito.never()).save(Mockito.any());

        Mockito.verify(orderItemRepository, Mockito.never()).saveAll(Mockito.any());

        Assertions.assertThat(businessException.getCode()).isEqualTo(OrderApiError.PRODUCT_INSUFFICIENT_STOCK_QUANTITY.getCode());
        Assertions.assertThat(businessException.getFormattedMessage())
                .isEqualTo(OrderApiError.PRODUCT_INSUFFICIENT_STOCK_QUANTITY.getMessage().formatted(item.getQuantity(), product.getId(), savedProduct.getStockQuantity()));

    }

}