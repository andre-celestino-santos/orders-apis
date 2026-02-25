package com.andre.orders_apis.handler;

import com.andre.orders_apis.dto.OrderApiErrorDto;
import com.andre.orders_apis.exception.BusinessException;
import com.andre.orders_apis.exception.ResourceNotFoundException;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.exc.InvalidFormatException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected @Nullable ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                            HttpHeaders headers,
                                                                            HttpStatusCode status,
                                                                            WebRequest request) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(errors);
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                            HttpHeaders headers,
                                                                            HttpStatusCode status,
                                                                            WebRequest request) {

        if (ex.getCause() instanceof InvalidFormatException exInvalid) {
            List<JacksonException.Reference> references = exInvalid.getPath();

            if (references == null) {
                return super.handleHttpMessageNotReadable(ex, headers, status, request);
            }

            Iterator<JacksonException.Reference> referencesIterator = references.iterator();

            Map<String, String> errors = new HashMap<>();
            StringBuilder propertyName = new StringBuilder();

            while (referencesIterator.hasNext()) {
                JacksonException.Reference reference = referencesIterator.next();
                if(reference.getIndex() < 0) {
                    propertyName.append(reference.getPropertyName());
                } else {
                    propertyName.append("[")
                                .append(reference.getIndex())
                                .append("]")
                                .append(".");
                }
            }

            errors.put(propertyName.toString(), ex.getMessage());
            return ResponseEntity.badRequest().body(errors);
        }

        return super.handleHttpMessageNotReadable(ex, headers, status, request);
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {

        if (ex instanceof MethodArgumentTypeMismatchException exInvalid) {
            Map<String, String> errors = new HashMap<>();
            errors.put(exInvalid.getPropertyName(), exInvalid.getMessage());
            return ResponseEntity.badRequest().body(errors);
        }

        return super.handleTypeMismatch(ex, headers, status, request);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<OrderApiErrorDto> handleBusinessException(BusinessException ex) {
        OrderApiErrorDto response = new OrderApiErrorDto();
        response.setCode(ex.getCode());
        response.setMessage(ex.getFormattedMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<OrderApiErrorDto> handleResourceNotFoundException(ResourceNotFoundException ex) {
        OrderApiErrorDto response = new OrderApiErrorDto();
        response.setCode(ex.getCode());
        response.setMessage(ex.getFormattedMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

}