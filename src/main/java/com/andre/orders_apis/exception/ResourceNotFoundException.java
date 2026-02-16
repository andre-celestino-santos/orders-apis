package com.andre.orders_apis.exception;

import com.andre.orders_apis.enums.OrderApiError;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(OrderApiError error, Object... args) {
        super(error, args);
    }

}