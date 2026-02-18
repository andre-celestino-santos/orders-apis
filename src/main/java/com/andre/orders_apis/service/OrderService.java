package com.andre.orders_apis.service;

import com.andre.orders_apis.entity.Order;
import com.andre.orders_apis.entity.OrderItem;
import com.andre.orders_apis.entity.OrderStatus;
import com.andre.orders_apis.entity.Product;
import com.andre.orders_apis.enums.OrderApiError;
import com.andre.orders_apis.exception.BusinessException;
import com.andre.orders_apis.exception.ResourceNotFoundException;
import com.andre.orders_apis.repository.OrderItemRepository;
import com.andre.orders_apis.repository.OrderRepository;
import com.andre.orders_apis.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public Order create(Order order) {
        Order savedOrder = orderRepository.save(order);

        for (OrderItem orderItem : order.getItems()) {

            orderItem.setOrder(savedOrder);

            Long productId = orderItem.getProduct().getId();

            Optional<Product> optProduct = productRepository.findByIdAndActiveTrueForUpdate(productId);

            if (optProduct.isEmpty()) {
                throw new ResourceNotFoundException(OrderApiError.PRODUCT_NOT_FOUND, productId);
            }

            Product product = optProduct.get();

            Integer requiredQuantity = orderItem.getQuantity();
            Integer stockQuantity = product.getStockQuantity();

            if (requiredQuantity > stockQuantity) {
                throw new BusinessException(OrderApiError.PRODUCT_INSUFFICIENT_STOCK_QUANTITY, requiredQuantity, productId, stockQuantity);
            }

            Integer newStockQuantity = stockQuantity - requiredQuantity;

            product.setStockQuantity(newStockQuantity);

            product = productRepository.save(product);

            orderItem.setProduct(product);
        }

        orderItemRepository.saveAll(order.getItems());

        return savedOrder;
    }

    @Transactional
    public void cancel(Long id) {
        Optional<Order> optOrder = orderRepository.findById(id);

        if (optOrder.isEmpty()) {
            throw new ResourceNotFoundException(OrderApiError.ORDER_NOT_FOUND, id);
        }

        Order order = optOrder.get();

        if (order.getStatus() == OrderStatus.CANCELLED) {
            return;
        }

        order.setStatus(OrderStatus.CANCELLED);

        orderRepository.save(order);

        List<OrderItem> items = orderItemRepository.findAllByOrder(order);

        for (OrderItem item : items) {
            Product product = item.getProduct();

            Integer newStockQuantity = product.getStockQuantity() + item.getQuantity();

            product.setStockQuantity(newStockQuantity);

            productRepository.save(product);
        }
    }

}