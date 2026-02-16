package com.andre.orders_apis.enums;

import lombok.Getter;

@Getter
public enum OrderApiError {
    PRODUCT_NOT_FOUND("ORD-001", "Product {} not found");

    private final String code;
    private final String message;

    OrderApiError(String code, String message) {
        this.code = code;
        this.message = message;
    }

}