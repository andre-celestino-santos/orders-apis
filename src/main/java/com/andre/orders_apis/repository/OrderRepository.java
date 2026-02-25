package com.andre.orders_apis.repository;

import com.andre.orders_apis.entity.Order;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends CrudRepository<Order, Long> {

    Optional<Order> findByPublicId(UUID publicId);

}