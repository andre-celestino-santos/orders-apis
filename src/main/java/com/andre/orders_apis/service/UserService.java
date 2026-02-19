package com.andre.orders_apis.service;

import com.andre.orders_apis.entity.Role;
import com.andre.orders_apis.entity.User;
import com.andre.orders_apis.enums.OrderApiError;
import com.andre.orders_apis.exception.BusinessException;
import com.andre.orders_apis.exception.ResourceNotFoundException;
import com.andre.orders_apis.repository.RoleRepository;
import com.andre.orders_apis.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User create(User user) {
        final String username = user.getUsername();
        Optional<User> optUser = userRepository.findByUsername(username);

        if (optUser.isPresent()) {
            throw new BusinessException(OrderApiError.USER_ALREADY_EXISTS, username);
        }

        final Set<Role> roles = user.getRoles();

        if (roles.isEmpty()) {
            throw new BusinessException(OrderApiError.CREATE_USER_REQUIRE_ROLE);
        }

        Set<Role> userRoles = new HashSet<>();
        for (Role role : user.getRoles()) {
            String roleName = role.getName();
            Optional<Role> optRole = roleRepository.findByName(roleName);
            if (optRole.isEmpty()) {
                throw new ResourceNotFoundException(OrderApiError.ROLE_NOT_FOUND, roleName);
            }
            userRoles.add(optRole.get());
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setRoles(userRoles);

        return userRepository.save(newUser);
    }

}