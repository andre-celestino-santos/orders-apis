package com.andre.orders_apis.repository;

import com.andre.orders_apis.entity.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Long> {

}