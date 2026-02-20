package com.andre.orders_apis.service;

import com.andre.orders_apis.entity.User;
import com.andre.orders_apis.enums.OrderApiError;
import com.andre.orders_apis.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @NullMarked
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optUser = userRepository.findByUsername(username);

        if (optUser.isEmpty()) {
            throw new UsernameNotFoundException(OrderApiError.USER_NOT_FOUND.getMessage());
        }

        User user = optUser.get();

        return toUserDetails(user);
    }

    private org.springframework.security.core.userdetails.User toUserDetails(User user) {
        List<SimpleGrantedAuthority> rolesUserDetails = user.getRoles().stream()
                .map(r -> new SimpleGrantedAuthority(r.getName()))
                .toList();

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                rolesUserDetails);
    }

}