package com.andre.orders_apis.mapper;

import com.andre.orders_apis.dto.UserRequestDto;
import com.andre.orders_apis.dto.UserResponseDto;
import com.andre.orders_apis.dto.UserRoleRequestDto;
import com.andre.orders_apis.dto.UserRoleResponseDto;
import com.andre.orders_apis.entity.Role;
import com.andre.orders_apis.entity.User;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class UserMapper {

    public User toEntity(UserRequestDto request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());

        Set<Role> roles = new HashSet<>();
        for (UserRoleRequestDto roleRequest : request.getRoles()) {
            Role role = new Role();
            role.setName(roleRequest.getName());
            roles.add(role);
        }
        user.setRoles(roles);

        return user;
    }

    public UserResponseDto toResponse(User entity) {
        UserResponseDto response = new UserResponseDto();
        response.setId(entity.getId());
        response.setUsername(entity.getUsername());

        Set<UserRoleResponseDto> rolesResponse = new HashSet<>();
        for (Role role : entity.getRoles()) {
            UserRoleResponseDto roleResponse = new UserRoleResponseDto();
            roleResponse.setName(role.getName());
            rolesResponse.add(roleResponse);
        }
        response.setRoles(rolesResponse);

        return response;
    }

}