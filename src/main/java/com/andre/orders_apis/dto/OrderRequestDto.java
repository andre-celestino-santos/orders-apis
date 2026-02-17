package com.andre.orders_apis.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequestDto {

    @NotBlank
    private String customerId;

    @Valid
    @NotEmpty
    private List<OrderItemRequestDto> items;

}