package com.andre.orders_apis.controller;

import com.andre.orders_apis.dto.UserRequestDto;
import com.andre.orders_apis.dto.UserResponseDto;
import com.andre.orders_apis.entity.User;
import com.andre.orders_apis.mapper.UserMapper;
import com.andre.orders_apis.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public ResponseEntity<UserResponseDto> create(@RequestBody @Valid UserRequestDto body) {
        User userRequest = userMapper.toEntity(body);

        User user = userService.create(userRequest);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(user.getId())
                .toUri();

        UserResponseDto response = userMapper.toResponse(user);

        return ResponseEntity.created(location).body(response);
    }

}