package com.andre.orders_apis.integration;

import com.andre.orders_apis.dto.AuthRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Sql(scripts = "classpath:auth.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class AuthIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldLoginSuccessfully() throws Exception {
        AuthRequestDto request = new AuthRequestDto();
        request.setUsername("test-admin-user");
        request.setPassword("test-admin-user");

        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    public void shouldReturnUnauthorizedWhenLoginWithInvalidCredentials() throws Exception {
        AuthRequestDto request = new AuthRequestDto();
        request.setUsername("not-found-user");
        request.setPassword("test-admin-user");

        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isUnauthorized());
    }

}