package com.andre.orders_apis.service;

import com.andre.orders_apis.entity.Authorization;
import com.andre.orders_apis.entity.User;
import com.andre.orders_apis.enums.OrderApiError;
import com.andre.orders_apis.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Test
    public void shouldCreateTokenSuccessfully() {
        User user = new User();
        user.setUsername("user");
        user.setPassword("pass");

        Optional<User> optUser = Optional.of(user);

        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(optUser);

        Mockito.when(passwordEncoder.matches(Mockito.any(), Mockito.any())).thenReturn(true);

        String token = "test-token";

        Mockito.when(jwtService.generateToken(Mockito.any())).thenReturn(token);

        Authorization authorization = new Authorization();
        authorization.setPassword("pass");
        authorization.setUsername("user");

        Authorization createdAuthentication = authService.login(authorization);

        Mockito.verify(userRepository, Mockito.atMostOnce()).findByUsername(Mockito.any());
        Mockito.verify(passwordEncoder, Mockito.atMostOnce()).matches(Mockito.any(), Mockito.any());
        Mockito.verify(jwtService, Mockito.atMostOnce()).generateToken(Mockito.any());

        Assertions.assertThat(createdAuthentication.getUsername()).isEqualTo(authorization.getUsername());
        Assertions.assertThat(createdAuthentication.getPassword()).isEqualTo(authorization.getPassword());
        Assertions.assertThat(createdAuthentication.getToken()).isEqualTo(token);
    }

    @Test
    public void shouldReturnExceptionWhenUserNotFound() {
        Optional<User> optUser = Optional.empty();

        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(optUser);

        Authorization authorization = new Authorization();
        authorization.setPassword("pass");
        authorization.setUsername("user");

        BadCredentialsException exception = Assertions.catchThrowableOfType(BadCredentialsException.class,
                () -> authService.login(authorization));

        Mockito.verify(userRepository, Mockito.atMostOnce()).findByUsername(Mockito.any());
        Mockito.verify(passwordEncoder, Mockito.never()).matches(Mockito.any(), Mockito.any());
        Mockito.verify(jwtService, Mockito.never()).generateToken(Mockito.any());

        Assertions.assertThat(exception.getMessage()).isEqualTo(OrderApiError.INVALID_CREDENTIALS.getMessage());
    }

    @Test
    public void shouldReturnExceptionWhenPasswordNotMatches() {
        User user = new User();
        user.setUsername("user");
        user.setPassword("pass");

        Optional<User> optUser = Optional.of(user);

        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(optUser);

        Mockito.when(passwordEncoder.matches(Mockito.any(), Mockito.any())).thenReturn(false);

        Authorization authorization = new Authorization();
        authorization.setPassword("pass");
        authorization.setUsername("user");

        BadCredentialsException exception = Assertions.catchThrowableOfType(BadCredentialsException.class,
                () -> authService.login(authorization));

        Mockito.verify(userRepository, Mockito.atMostOnce()).findByUsername(Mockito.any());
        Mockito.verify(passwordEncoder, Mockito.atMostOnce()).matches(Mockito.any(), Mockito.any());
        Mockito.verify(jwtService, Mockito.never()).generateToken(Mockito.any());

        Assertions.assertThat(exception.getMessage()).isEqualTo(OrderApiError.INVALID_CREDENTIALS.getMessage());
    }

}