package com.andre.orders_apis.controller;

import com.andre.orders_apis.dto.OrderRequestDto;
import com.andre.orders_apis.dto.OrderResponseDto;
import com.andre.orders_apis.entity.Order;
import com.andre.orders_apis.mapper.OrderMapper;
import com.andre.orders_apis.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @PostMapping
    public ResponseEntity<OrderResponseDto> create(@RequestBody @Valid OrderRequestDto body) {
        Order orderRequest = orderMapper.toEntity(body);

        Order order = orderService.create(orderRequest);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(order.getId())
                .toUri();

        OrderResponseDto response = orderMapper.toResponse(order);

        return ResponseEntity.created(location).body(response);
    }

}