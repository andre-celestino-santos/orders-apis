package com.andre.orders_apis.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderApiErrorDto {
    private String code;
    private String message;
}
