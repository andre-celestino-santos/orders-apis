package com.andre.orders_apis.exception;

import com.andre.orders_apis.enums.OrderApiError;

public class BusinessException extends RuntimeException {

    private final OrderApiError error;
    private final Object[] args;

    public BusinessException(OrderApiError error, Object... args) {
        this.error = error;
        this.args = args;
    }

    public String getCode() {
        return error.getCode();
    }

    public String getFormattedMessage() {
        return error.getMessage().formatted(args);
    }

}