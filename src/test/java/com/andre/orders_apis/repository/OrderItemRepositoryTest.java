package com.andre.orders_apis.repository;

import com.andre.orders_apis.entity.Category;
import com.andre.orders_apis.entity.Order;
import com.andre.orders_apis.entity.OrderItem;
import com.andre.orders_apis.entity.Product;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;

@DataJpaTest
public class OrderItemRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Test
    public void shouldCreateOrderItemSuccessfully() {
        createOrderItem();
    }

    private Product createProduct() {
        Product product = new Product();
        product.setBrand("Samsung");
        product.setModel("A07");
        product.setPrice(new BigDecimal("594.00"));
        product.setCategory(Category.SMARTPHONE);
        product.setStockQuantity(5);
        product.setDescription("Samsung Galaxy A07 128gb, 4gb");
        return productRepository.save(product);
    }

    private Order createOrder() {
        Order order = new Order();
        order.setCustomerId("1234");
        return orderRepository.save(order);
    }

    private void createOrderItem() {
        Product savedProduct = createProduct();
        Order savedOrder = createOrder();

        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(13);

        orderItem.setProduct(savedProduct);
        orderItem.setOrder(savedOrder);

        OrderItem savedOrderItem = orderItemRepository.save(orderItem);

        Assertions.assertThat(savedOrderItem.getId()).isGreaterThan(0);
        Assertions.assertThat(savedOrderItem.getQuantity()).isEqualTo(13);
    }

}