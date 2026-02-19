package com.andre.orders_apis.integration;

import com.andre.orders_apis.entity.User;
import com.andre.orders_apis.repository.UserRepository;
import com.andre.orders_apis.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TokenGeneratorTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    public String getToken(String username) {
        User createdUser = userRepository.findByUsername(username).orElseThrow();

        return "Bearer " + jwtService.generateToken(createdUser);
    }

}