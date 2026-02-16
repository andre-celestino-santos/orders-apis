package com.andre.orders_apis.repository;

import com.andre.orders_apis.entity.Order;
import com.andre.orders_apis.entity.OrderItem;
import com.andre.orders_apis.entity.OrderStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.ArrayList;
import java.util.List;

@DataJpaTest
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    public void shouldCreateOrderSuccessfully() {
        createOrder();
    }

    private void createOrder() {
        Order order = new Order();
        order.setCustomerId("1234");

        List<OrderItem> items = new ArrayList<>();
        OrderItem orderItem = new OrderItem();
        items.add(orderItem);

        order.setItems(items);

        Order savedOrder = orderRepository.save(order);

        Assertions.assertThat(savedOrder.getId()).isGreaterThan(0);
        Assertions.assertThat(savedOrder.getCustomerId()).isEqualTo(order.getCustomerId());
        Assertions.assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.CREATED);
        Assertions.assertThat(savedOrder.getCreatedAt()).isNotNull();
        Assertions.assertThat(savedOrder.getUpdatedAt()).isNotNull();

        Assertions.assertThat(savedOrder.getItems().get(0).getId()).isNull();
    }

}