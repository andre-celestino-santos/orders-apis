package com.andre.orders_apis.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemRequestDto {

    @NotNull
    @Min(1)
    private Long id;

    @NotNull
    @Min(1)
    private Integer quantity;

}