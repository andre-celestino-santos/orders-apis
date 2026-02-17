package com.andre.orders_apis.mapper;

import com.andre.orders_apis.dto.OrderItemRequestDto;
import com.andre.orders_apis.dto.OrderItemResponseDto;
import com.andre.orders_apis.dto.OrderRequestDto;
import com.andre.orders_apis.dto.OrderResponseDto;
import com.andre.orders_apis.entity.Order;
import com.andre.orders_apis.entity.OrderItem;
import com.andre.orders_apis.entity.OrderStatus;
import com.andre.orders_apis.entity.Product;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderMapperTest {

    private final OrderMapper orderMapper = new OrderMapper();

    @Test
    public void shouldReturnEntitySuccessfully() {
        OrderRequestDto request = new OrderRequestDto();
        request.setCustomerId("abc123");

        List<OrderItemRequestDto> items = new ArrayList<>();
        OrderItemRequestDto item = new OrderItemRequestDto();
        item.setId(1L);
        item.setQuantity(10);
        items.add(item);

        request.setItems(items);

        Order entity = orderMapper.toEntity(request);

        Assertions.assertThat(entity).isNotNull();
        Assertions.assertThat(entity.getId()).isNull();
        Assertions.assertThat(entity.getCustomerId()).isEqualTo(request.getCustomerId());
        Assertions.assertThat(entity.getStatus()).isEqualTo(OrderStatus.CREATED);
        Assertions.assertThat(entity.getCreatedAt()).isNull();
        Assertions.assertThat(entity.getUpdatedAt()).isNull();

        List<OrderItem> entityItems = entity.getItems();
        Assertions.assertThat(entityItems).hasSize(1);

        OrderItem itemEntity = entityItems.get(0);
        Assertions.assertThat(itemEntity.getId()).isNull();
        Assertions.assertThat(itemEntity.getQuantity()).isEqualTo(item.getQuantity());
        Assertions.assertThat(itemEntity.getOrder()).isNull();

        Product productEntity = itemEntity.getProduct();
        Assertions.assertThat(productEntity).isNotNull();
        Assertions.assertThat(productEntity.getId()).isEqualTo(item.getId());
    }

    @Test
    public void shouldReturnResponseSuccessfully() {
        Order entity = new Order();
        entity.setId(1L);
        entity.setCustomerId("abc123");
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        List<OrderItem> itemsEntity = new ArrayList<>();
        OrderItem itemEntity = new OrderItem();
        Product product = new Product();
        product.setId(5L);
        itemEntity.setProduct(product);
        itemEntity.setQuantity(7);
        itemEntity.setCreatedAt(LocalDateTime.now());
        itemsEntity.add(itemEntity);
        entity.setItems(itemsEntity);
        
        OrderResponseDto response = orderMapper.toResponse(entity);
        
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getId()).isEqualTo(entity.getId());
        Assertions.assertThat(response.getCustomerId()).isEqualTo(entity.getCustomerId());
        Assertions.assertThat(response.getStatus()).isEqualTo(entity.getStatus());
        Assertions.assertThat(response.getCreatedAt()).isEqualTo(entity.getCreatedAt());
        Assertions.assertThat(response.getUpdatedAt()).isEqualTo(entity.getUpdatedAt());

        List<OrderItemResponseDto> responseItems = response.getItems();
        Assertions.assertThat(responseItems).hasSize(1);

        OrderItemResponseDto responseItem = responseItems.get(0);
        Assertions.assertThat(responseItem.getId()).isEqualTo(itemEntity.getProduct().getId());
        Assertions.assertThat(responseItem.getQuantity()).isEqualTo(itemEntity.getQuantity());
        Assertions.assertThat(responseItem.getCreatedAt()).isEqualTo(itemEntity.getCreatedAt());
    }

}