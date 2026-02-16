package com.andre.orders_apis.repository;

import com.andre.orders_apis.entity.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProductRepository extends CrudRepository<Product, Integer> {

    Optional<Product> findByIdAndActiveTrue(Integer id);

}