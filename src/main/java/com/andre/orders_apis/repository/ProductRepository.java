package com.andre.orders_apis.repository;

import com.andre.orders_apis.entity.Category;
import com.andre.orders_apis.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProductRepository extends CrudRepository<Product, Long> {

    Optional<Product> findByIdAndActiveTrue(Long id);

    Page<Product> findAllByCategoryAndActiveTrue(Category category, Pageable pageable);

}