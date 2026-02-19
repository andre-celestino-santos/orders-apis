package com.andre.orders_apis.service;

import com.andre.orders_apis.entity.Role;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private UserRepository userRepository;

    @Test
    public void shouldCreateUserDetailsSuccessfully() {
        User user = new User();
        user.setUsername("user");
        user.setPassword("pass");

        Role roleOne = new Role();
        roleOne.setName("ROLE_ONE");

        Role roleTwo = new Role();
        roleTwo.setName("ROLE_TWO");

        Set<Role> roles = Set.of(roleOne, roleTwo);

        user.setRoles(roles);

        Optional<User> optUser = Optional.of(user);

        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(optUser);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("user");

        Mockito.verify(userRepository, Mockito.atMostOnce()).findByUsername(Mockito.any());

        Assertions.assertThat(userDetails.getUsername()).isEqualTo(user.getUsername());
        Assertions.assertThat(userDetails.getPassword()).isEqualTo(user.getPassword());

        Collection<? extends GrantedAuthority> rolesUserDetails = userDetails.getAuthorities();

        Assertions.assertThat(rolesUserDetails).hasSize(2);

        long rolesUserDetailsCount = rolesUserDetails.stream()
                .filter(grantedAuthority -> grantedAuthority.getAuthority() != null)
                .filter(grantedAuthority ->
                    grantedAuthority.getAuthority().equals(roleOne.getName()) || grantedAuthority.getAuthority().equals(roleTwo.getName())
                )
                .count();

        Assertions.assertThat(rolesUserDetailsCount).isEqualTo(2);
    }

    @Test
    public void shouldReturnExceptionWhenUserNotFound() {
        Optional<User> optUser = Optional.empty();

        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(optUser);

        UsernameNotFoundException exception = Assertions.catchThrowableOfType(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("user"));

        Mockito.verify(userRepository, Mockito.atMostOnce()).findByUsername(Mockito.any());

        Assertions.assertThat(exception.getMessage()).isEqualTo(OrderApiError.USER_NOT_FOUND.getMessage());
    }

}