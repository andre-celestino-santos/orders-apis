package com.andre.orders_apis.mapper;

import com.andre.orders_apis.dto.AuthRequestDto;
import com.andre.orders_apis.dto.AuthResponseDto;
import com.andre.orders_apis.entity.Authorization;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    public Authorization toEntity(AuthRequestDto request) {
        Authorization entity = new Authorization();
        entity.setUsername(request.getUsername());
        entity.setPassword(request.getPassword());
        return entity;
    }

    public AuthResponseDto toResponse(Authorization entity) {
        AuthResponseDto response = new AuthResponseDto();
        response.setToken(entity.getToken());
        return response;
    }

}