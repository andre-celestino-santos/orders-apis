package com.andre.orders_apis.controller;

import com.andre.orders_apis.dto.UserRequestDto;
import com.andre.orders_apis.dto.UserResponseDto;
import com.andre.orders_apis.dto.UserRoleRequestDto;
import com.andre.orders_apis.dto.UserRoleResponseDto;
import com.andre.orders_apis.entity.User;
import com.andre.orders_apis.filter.JwtAuthenticationFilter;
import com.andre.orders_apis.mapper.UserMapper;
import com.andre.orders_apis.service.UserService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserMapper userMapper;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    public void shouldCreateUserSuccessfully() throws Exception {
        Mockito.when(userMapper.toEntity(Mockito.any())).thenReturn(new User());

        User user = new User();
        user.setId(32L);

        Mockito.when(userService.create(Mockito.any())).thenReturn(user);

        UserRequestDto request = new UserRequestDto();
        request.setUsername("test-user");
        request.setPassword("test-pass-user");

        UserRoleRequestDto roleRequest = new UserRoleRequestDto();
        roleRequest.setName("ROLE_USER");
        request.setRoles(Set.of(roleRequest));

        UserResponseDto response = new UserResponseDto();
        response.setId(user.getId());
        response.setUsername(request.getUsername());
        UserRoleResponseDto roleResponse = new UserRoleResponseDto();
        roleResponse.setName(roleRequest.getName());
        response.setRoles(Set.of(roleResponse));

        Mockito.when(userMapper.toResponse(Mockito.any())).thenReturn(response);

        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.username").value(response.getUsername()))
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.roles").isNotEmpty())
                .andExpect(jsonPath("$.roles[0].name").value(roleResponse.getName()))
                .andExpect(header().string(HttpHeaders.LOCATION, Matchers.endsWith("/v1/users/%s".formatted(response.getId()))));

        Mockito.verify(userMapper, Mockito.atMostOnce()).toEntity(Mockito.any());
        Mockito.verify(userService, Mockito.atMostOnce()).create(Mockito.any());
        Mockito.verify(userMapper, Mockito.atMostOnce()).toResponse(Mockito.any());
    }

    @Test
    public void shouldReturnBadRequestWithNullFields() throws Exception {
        UserRequestDto request = new UserRequestDto();

        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username").value("must not be blank"))
                .andExpect(jsonPath("$.password").value("must not be blank"))
                .andExpect(jsonPath("$.roles").value("must not be empty"));

        Mockito.verify(userMapper, Mockito.never()).toEntity(Mockito.any());
        Mockito.verify(userService, Mockito.never()).create(Mockito.any());
        Mockito.verify(userMapper, Mockito.never()).toResponse(Mockito.any());
    }

}