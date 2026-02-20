package com.andre.orders_apis.service;

import com.andre.orders_apis.entity.Authorization;
import com.andre.orders_apis.entity.User;
import com.andre.orders_apis.enums.OrderApiError;
import com.andre.orders_apis.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public Authorization login(Authorization authorization) {
        Optional<User> optUser = userRepository.findByUsername(authorization.getUsername());

        if (optUser.isEmpty()) {
            throw new BadCredentialsException(OrderApiError.INVALID_CREDENTIALS.getMessage());
        }

        User savedUser = optUser.get();

        if (!passwordEncoder.matches(authorization.getPassword(), savedUser.getPassword())) {
            throw new BadCredentialsException(OrderApiError.INVALID_CREDENTIALS.getMessage());
        }

        String token = jwtService.generateToken(savedUser);

        Authorization createdAuthorization = new Authorization();
        createdAuthorization.setUsername(authorization.getUsername());
        createdAuthorization.setPassword(authorization.getPassword());
        createdAuthorization.setToken(token);

        return createdAuthorization;
    }

}