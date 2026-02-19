package com.andre.orders_apis.controller;

import com.andre.orders_apis.dto.AuthRequestDto;
import com.andre.orders_apis.dto.AuthResponseDto;
import com.andre.orders_apis.entity.Authorization;
import com.andre.orders_apis.filter.JwtAuthenticationFilter;
import com.andre.orders_apis.mapper.AuthMapper;
import com.andre.orders_apis.service.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private AuthMapper authMapper;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    public void shouldLoginSuccessfully() throws Exception {
        AuthRequestDto request = new AuthRequestDto();
        request.setUsername("user");
        request.setPassword("pass");

        Mockito.when(authMapper.toEntity(Mockito.any())).thenReturn(new Authorization());

        Mockito.when(authService.login(Mockito.any())).thenReturn(new Authorization());

        AuthResponseDto response = new AuthResponseDto();
        response.setToken("test-token");

        Mockito.when(authMapper.toResponse(Mockito.any())).thenReturn(response);

        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(response.getToken()));

        Mockito.verify(authMapper, Mockito.atMostOnce()).toEntity(Mockito.any());
        Mockito.verify(authService, Mockito.atMostOnce()).login(Mockito.any());
        Mockito.verify(authMapper, Mockito.atMostOnce()).toResponse(Mockito.any());
    }

    @Test
    public void shouldReturnBadRequestWhenLoginWithNullUsername() throws Exception {
        AuthRequestDto request = new AuthRequestDto();
        request.setUsername(null);
        request.setPassword("pass");

        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username").value("must not be blank"));

        Mockito.verify(authMapper, Mockito.never()).toEntity(Mockito.any());
        Mockito.verify(authService, Mockito.never()).login(Mockito.any());
        Mockito.verify(authMapper, Mockito.never()).toResponse(Mockito.any());
    }

}