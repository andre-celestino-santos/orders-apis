package com.andre.orders_apis.repository;

import com.andre.orders_apis.entity.Order;
import com.andre.orders_apis.entity.OrderItem;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OrderItemRepository extends CrudRepository<OrderItem, Long> {

    List<OrderItem> findAllByOrder(Order order);

}