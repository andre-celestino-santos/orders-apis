package com.andre.orders_apis.repository;

import com.andre.orders_apis.entity.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Integer> {

}