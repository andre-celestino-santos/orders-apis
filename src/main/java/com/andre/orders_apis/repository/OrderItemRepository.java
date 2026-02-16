package com.andre.orders_apis.repository;

import com.andre.orders_apis.entity.OrderItem;
import org.springframework.data.repository.CrudRepository;

public interface OrderItemRepository extends CrudRepository<OrderItem, Long> {

}