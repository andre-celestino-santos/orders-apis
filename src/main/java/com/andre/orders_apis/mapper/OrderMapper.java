package com.andre.orders_apis.mapper;

import com.andre.orders_apis.dto.OrderItemRequestDto;
import com.andre.orders_apis.dto.OrderItemResponseDto;
import com.andre.orders_apis.dto.OrderRequestDto;
import com.andre.orders_apis.dto.OrderResponseDto;
import com.andre.orders_apis.entity.Order;
import com.andre.orders_apis.entity.OrderItem;
import com.andre.orders_apis.entity.Product;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class OrderMapper {

    public Order toEntity(OrderRequestDto request) {
        Order entity = new Order();
        entity.setCustomerId(request.getCustomerId());
        List<OrderItem> itemsEntity = request.getItems().stream().map(this::toEntity).toList();
        entity.setItems(itemsEntity);
        return entity;
    }

    public Order toEntity(UUID publicId, OrderItemRequestDto request) {
        Order entity = new Order();
        entity.setPublicId(publicId);
        List<OrderItem> items = new ArrayList<>();
        OrderItem item = toEntity(request);
        items.add(item);
        entity.setItems(items);
        return entity;
    }

    public OrderResponseDto toResponse(Order entity) {
        OrderResponseDto response = new OrderResponseDto();
        response.setId(entity.getPublicId());
        response.setCustomerId(entity.getCustomerId());
        response.setStatus(entity.getStatus());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        List<OrderItemResponseDto> responseItems = entity.getItems().stream().map(this::toResponse).toList();
        response.setItems(responseItems);
        return response;
    }

    private OrderItem toEntity(OrderItemRequestDto request) {
        OrderItem entity = new OrderItem();
        entity.setQuantity(request.getQuantity());
        Product productEntity = new Product();
        productEntity.setPublicId(request.getId());
        entity.setProduct(productEntity);
        return entity;
    }

    public OrderItemResponseDto toResponse(OrderItem entity) {
        OrderItemResponseDto response = new OrderItemResponseDto();
        response.setId(entity.getProduct().getPublicId());
        response.setQuantity(entity.getQuantity());
        response.setCreatedAt(entity.getCreatedAt());
        return response;
    }

}