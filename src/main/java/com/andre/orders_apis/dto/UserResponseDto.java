package com.andre.orders_apis.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserResponseDto {

    private Long id;

    private String username;

    private Set<UserRoleResponseDto> roles;

}