package com.andre.orders_apis.controller;

import com.andre.orders_apis.dto.AuthRequestDto;
import com.andre.orders_apis.dto.AuthResponseDto;
import com.andre.orders_apis.entity.Authorization;
import com.andre.orders_apis.mapper.AuthMapper;
import com.andre.orders_apis.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthMapper authMapper;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody @Valid AuthRequestDto body) {
        Authorization authorizationRequest = authMapper.toEntity(body);

        Authorization authorization = authService.login(authorizationRequest);

        AuthResponseDto response = authMapper.toResponse(authorization);

        return ResponseEntity.ok(response);
    }

}