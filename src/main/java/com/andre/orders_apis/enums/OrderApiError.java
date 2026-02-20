package com.andre.orders_apis.enums;

import lombok.Getter;

@Getter
public enum OrderApiError {
    PRODUCT_NOT_FOUND("ORD-001", "Product %s not found"),
    PRODUCT_INSUFFICIENT_STOCK_QUANTITY("ORD-002", "Quantity required %s, product %s with available quantity %s"),

    ORDER_NOT_FOUND("ORD-003", "Order %s not found"),

    USER_ALREADY_EXISTS("ORD-004", "User %s already exists"),

    INVALID_CREDENTIALS("ORD-005", "Invalid credentials"),
    USER_NOT_FOUND("ORD-006", "User not found");

    private final String code;
    private final String message;

    OrderApiError(String code, String message) {
        this.code = code;
        this.message = message;
    }

}