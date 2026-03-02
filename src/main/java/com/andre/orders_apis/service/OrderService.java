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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public Order create(Order order) {
        Order savedOrder = orderRepository.save(order);

        addItem(order, savedOrder);

        return savedOrder;
    }

    @Transactional
    public Order addItem(Order order) {
        Optional<Order> optOrder = orderRepository.findByPublicId(order.getPublicId());

        if (optOrder.isEmpty()) {
            throw new ResourceNotFoundException(OrderApiError.ORDER_NOT_FOUND, order.getPublicId());
        }

        Order savedOrder = optOrder.get();

        addItem(order, savedOrder);

        Order newSavedOrder = new Order();

        OrderItem orderItem = order.getItems().get(0);

        List<OrderItem> items = new ArrayList<>();

        for (OrderItem item : savedOrder.getItems()) {
            if (orderItem.getProduct().getPublicId().equals(item.getProduct().getPublicId())) {
                OrderItem newSavedItem = new OrderItem();
                newSavedItem.setProduct(item.getProduct());
                newSavedItem.setQuantity(item.getQuantity());
                newSavedItem.setCreatedAt(item.getCreatedAt());
                items.add(newSavedItem);
                break;
            }
        }

        newSavedOrder.setItems(items);

        return newSavedOrder;
    }

    @Transactional
    public void cancel(UUID publicId) {
        Optional<Order> optOrder = orderRepository.findByPublicId(publicId);

        if (optOrder.isEmpty()) {
            throw new ResourceNotFoundException(OrderApiError.ORDER_NOT_FOUND, publicId);
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

    private void addItem(Order order, Order savedOrder) {
        for (OrderItem orderItem : order.getItems()) {

            orderItem.setOrder(savedOrder);

            UUID publicId = orderItem.getProduct().getPublicId();

            Optional<Product> optProduct = productRepository.findByPublicIdAndActiveTrueForUpdate(publicId);

            if (optProduct.isEmpty()) {
                throw new ResourceNotFoundException(OrderApiError.PRODUCT_NOT_FOUND, publicId);
            }

            Product product = optProduct.get();

            Integer requiredQuantity = orderItem.getQuantity();
            Integer stockQuantity = product.getStockQuantity();

            if (requiredQuantity > stockQuantity) {
                throw new BusinessException(OrderApiError.PRODUCT_INSUFFICIENT_STOCK_QUANTITY, requiredQuantity, publicId, stockQuantity);
            }

            Integer newStockQuantity = stockQuantity - requiredQuantity;

            product.setStockQuantity(newStockQuantity);

            product = productRepository.save(product);

            orderItem.setProduct(product);
        }

        orderItemRepository.saveAll(order.getItems());
    }

}