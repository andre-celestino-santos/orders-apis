package com.andre.orders_apis.repository;

import com.andre.orders_apis.entity.Category;
import com.andre.orders_apis.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProductRepository extends CrudRepository<Product, Long> {

    Optional<Product> findByIdAndActiveTrue(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id and p.active = true")
    Optional<Product> findByIdAndActiveTrueForUpdate(Long id);

    Page<Product> findAllByCategoryAndActiveTrue(Category category, Pageable pageable);

}