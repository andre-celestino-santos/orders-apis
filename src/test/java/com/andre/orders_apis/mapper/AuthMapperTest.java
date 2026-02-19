package com.andre.orders_apis.mapper;

import com.andre.orders_apis.dto.AuthRequestDto;
import com.andre.orders_apis.dto.AuthResponseDto;
import com.andre.orders_apis.entity.Authorization;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class AuthMapperTest {

    private final AuthMapper authMapper = new AuthMapper();

    @Test
    public void shouldReturnEntitySuccessfully () {
        AuthRequestDto request = new AuthRequestDto();
        request.setUsername("user");
        request.setPassword("pass");

        Authorization entity = authMapper.toEntity(request);

        Assertions.assertThat(entity.getUsername()).isEqualTo(request.getUsername());
        Assertions.assertThat(entity.getPassword()).isEqualTo(request.getPassword());
        Assertions.assertThat(entity.getToken()).isNull();
    }

    @Test
    public void shouldReturnResponseSuccessfully() {
        Authorization entity = new Authorization();
        entity.setToken("test-token");

        AuthResponseDto response = authMapper.toResponse(entity);

        Assertions.assertThat(response.getToken()).isEqualTo(entity.getToken());
    }

}