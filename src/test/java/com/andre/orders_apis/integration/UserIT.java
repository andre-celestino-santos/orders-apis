package com.andre.orders_apis.integration;

import com.andre.orders_apis.dto.UserRequestDto;
import com.andre.orders_apis.dto.UserResponseDto;
import com.andre.orders_apis.dto.UserRoleRequestDto;
import com.andre.orders_apis.entity.User;
import com.andre.orders_apis.enums.OrderApiError;
import com.andre.orders_apis.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Sql(scripts = "classpath:auth.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class UserIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void shouldCreateUserSuccessfully() throws Exception {
        UserRequestDto request = new UserRequestDto();
        request.setUsername("new-user");
        request.setPassword("new-pass");

        UserRoleRequestDto roleRequest = new UserRoleRequestDto();
        roleRequest.setName("ROLE_USER");
        Set<UserRoleRequestDto> rolesRequest = Set.of(roleRequest);
        request.setRoles(rolesRequest);

        String content = objectMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isCreated())
                .andReturn();

        String responseAsString = result.getResponse().getContentAsString();
        UserResponseDto response = objectMapper.readValue(responseAsString, UserResponseDto.class);

        String headerLocationExpected = "/v1/users/%s".formatted(response.getId());
        String headerLocationResponse = result.getResponse().getHeader(HttpHeaders.LOCATION);
        Assertions.assertThat(headerLocationResponse).endsWith(headerLocationExpected);

        Assertions.assertThat(response.getUsername()).isEqualTo(request.getUsername());
        Assertions.assertThat(response.getRoles()).hasSize(1);
        Assertions.assertThat(response.getRoles().iterator().next().getName()).isEqualTo(roleRequest.getName());

        Optional<User> optSavedUser = userRepository.findByUsername(request.getUsername());
        Assertions.assertThat(optSavedUser).isPresent();
        User savedUser = optSavedUser.get();
        Assertions.assertThat(savedUser.getUsername()).isEqualTo(request.getUsername());
        boolean isPasswordMatches = passwordEncoder.matches(request.getPassword(), savedUser.getPassword());
        Assertions.assertThat(isPasswordMatches).isTrue();
        Assertions.assertThat(savedUser.getRoles()).hasSize(rolesRequest.size());
        Assertions.assertThat(savedUser.getRoles().iterator().next().getName()).isEqualTo(roleRequest.getName());
    }

    @Test
    public void shouldReturnBadRequestWhenCreateUserAlreadyExists() throws Exception {
        UserRequestDto request = new UserRequestDto();
        request.setUsername("test-user");
        request.setPassword("test-pass");

        UserRoleRequestDto roleRequest = new UserRoleRequestDto();
        roleRequest.setName("ROLE_USER");
        Set<UserRoleRequestDto> rolesRequest = Set.of(roleRequest);
        request.setRoles(rolesRequest);

        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(OrderApiError.USER_ALREADY_EXISTS.getCode()))
                .andExpect(jsonPath("$.message").value(OrderApiError.USER_ALREADY_EXISTS.getMessage().formatted(request.getUsername())));
    }

    @Test
    public void shouldReturnBadRequestWhenCreateUserWithoutRoles() throws Exception {
        UserRequestDto request = new UserRequestDto();
        request.setUsername("2-new-user");
        request.setPassword("2-new-pass");

        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.roles").value("must not be empty"));
    }

    @Test
    public void shouldReturnBadRequestWhenCreateUserAndRoleNotFound() throws Exception {
        UserRequestDto request = new UserRequestDto();
        request.setUsername("3-new-user");
        request.setPassword("3-new-pass");

        UserRoleRequestDto roleRequest = new UserRoleRequestDto();
        roleRequest.setName("ROLE_NOT_FOUND");
        Set<UserRoleRequestDto> rolesRequest = Set.of(roleRequest);
        request.setRoles(rolesRequest);

        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(".code").value(OrderApiError.ROLE_NOT_FOUND.getCode()))
                .andExpect(jsonPath(".message").value(OrderApiError.ROLE_NOT_FOUND.getMessage().formatted(roleRequest.getName())));
    }

}