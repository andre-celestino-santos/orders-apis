package com.andre.orders_apis.mapper;

import com.andre.orders_apis.dto.UserRequestDto;
import com.andre.orders_apis.dto.UserResponseDto;
import com.andre.orders_apis.dto.UserRoleRequestDto;
import com.andre.orders_apis.entity.Role;
import com.andre.orders_apis.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class UserMapperTest {

    private final UserMapper userMapper = new UserMapper();

    @Test
    public void shouldReturnEntitySuccessfully() {
        UserRequestDto request = new UserRequestDto();
        request.setUsername("test-user");
        request.setPassword("test-pass-user");

        UserRoleRequestDto roleRequest = new UserRoleRequestDto();
        roleRequest.setName("ROLE_USER");

        request.setRoles(Set.of(roleRequest));

        User entity = userMapper.toEntity(request);

        Assertions.assertThat(entity.getUsername()).isEqualTo(request.getUsername());
        Assertions.assertThat(entity.getPassword()).isEqualTo(request.getPassword());
        Assertions.assertThat(entity.getRoles()).hasSize(request.getRoles().size());
        Assertions.assertThat(entity.getRoles().iterator().next().getName()).isEqualTo(roleRequest.getName());
    }

    @Test
    public void shouldReturnResponseSuccessfully() {
        User user = new User();
        user.setId(85L);
        user.setUsername("test-user");
        user.setPassword("test-pass-user");

        Role roleAdmin = new Role();
        roleAdmin.setName("ROLE_ADMIN");

        user.setRoles(Set.of(roleAdmin));

        UserResponseDto response = userMapper.toResponse(user);

        Assertions.assertThat(response.getId()).isEqualTo(user.getId());
        Assertions.assertThat(response.getUsername()).isEqualTo(user.getUsername());
        Assertions.assertThat(response.getRoles()).hasSize(user.getRoles().size());
        Assertions.assertThat(response.getRoles().iterator().next().getName()).isEqualTo(roleAdmin.getName());
    }

}