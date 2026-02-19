package com.andre.orders_apis.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserResponseDto {

    private String username;

    private String password;

    private List<UserRoleRequestDto> roles;

}