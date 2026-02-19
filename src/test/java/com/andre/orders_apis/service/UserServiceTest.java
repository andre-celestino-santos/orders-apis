package com.andre.orders_apis.service;

import com.andre.orders_apis.entity.Role;
import com.andre.orders_apis.entity.User;
import com.andre.orders_apis.enums.OrderApiError;
import com.andre.orders_apis.exception.BusinessException;
import com.andre.orders_apis.exception.ResourceNotFoundException;
import com.andre.orders_apis.repository.RoleRepository;
import com.andre.orders_apis.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Test
    public void shouldCreateUserSuccessfully() {
        Optional<User> optUser = Optional.empty();

        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(optUser);

        Role roleAdmin = new Role();
        roleAdmin.setName("ROLE_ADMIN");

        Optional<Role> optRole = Optional.of(roleAdmin);

        Mockito.when(roleRepository.findByName(Mockito.any())).thenReturn(optRole);

        final String encodedPassword = "encoded-password";

        Mockito.when(passwordEncoder.encode(Mockito.any())).thenReturn(encodedPassword);

        Mockito.when(userRepository.save(userCaptor.capture())).thenReturn(new User());

        User user = new User();
        user.setUsername("test-user");
        user.setPassword("test-pass-user");

        Set<Role> roles = Set.of(roleAdmin);

        user.setRoles(roles);

        User createdUser = userService.create(user);

        Assertions.assertThat(createdUser).isNotNull();

        Mockito.verify(userRepository, Mockito.atMostOnce()).findByUsername(Mockito.any());
        Mockito.verify(roleRepository, Mockito.atMostOnce()).findByName(Mockito.any());
        Mockito.verify(passwordEncoder, Mockito.atMostOnce()).encode(Mockito.any());
        Mockito.verify(userRepository, Mockito.atMostOnce()).save(Mockito.any());

        User captorUserValue = userCaptor.getValue();

        Assertions.assertThat(captorUserValue.getUsername()).isEqualTo(user.getUsername());
        Assertions.assertThat(captorUserValue.getPassword()).isEqualTo(encodedPassword);
        Assertions.assertThat(captorUserValue.getRoles()).hasSize(1);

        long roleAdminCount = captorUserValue.getRoles().stream()
                .filter(role -> role.getName().equals("ROLE_ADMIN"))
                .count();

        Assertions.assertThat(roleAdminCount).isEqualTo(1);
    }

    @Test
    public void shouldReturnExceptionWhenCreateUserAlreadyExists() {
        User user = new User();
        user.setUsername("test-user");

        Optional<User> optUser = Optional.of(user);

        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(optUser);

        BusinessException exception = Assertions.catchThrowableOfType(BusinessException.class,
                () -> userService.create(user));

        Mockito.verify(userRepository, Mockito.atMostOnce()).findByUsername(Mockito.any());
        Mockito.verify(roleRepository, Mockito.never()).findByName(Mockito.any());
        Mockito.verify(passwordEncoder, Mockito.never()).encode(Mockito.any());
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());

        Assertions.assertThat(exception.getCode()).isEqualTo(OrderApiError.USER_ALREADY_EXISTS.getCode());
        Assertions.assertThat(exception.getFormattedMessage()).isEqualTo(OrderApiError.USER_ALREADY_EXISTS.getMessage().formatted(user.getUsername()));
    }

    @Test
    public void shouldReturnExceptionWhenCreateUserWithoutRoles() {
        Optional<User> optUser = Optional.empty();

        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(optUser);

        User user = new User();
        user.setUsername("test-user");
        user.setPassword("test-pass-user");
        user.setRoles(Set.of());

        BusinessException exception = Assertions.catchThrowableOfType(BusinessException.class,
                () -> userService.create(user));

        Mockito.verify(userRepository, Mockito.atMostOnce()).findByUsername(Mockito.any());
        Mockito.verify(roleRepository, Mockito.never()).findByName(Mockito.any());
        Mockito.verify(passwordEncoder, Mockito.never()).encode(Mockito.any());
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());

        Assertions.assertThat(exception.getCode()).isEqualTo(OrderApiError.CREATE_USER_REQUIRE_ROLE.getCode());
        Assertions.assertThat(exception.getFormattedMessage()).isEqualTo(OrderApiError.CREATE_USER_REQUIRE_ROLE.getMessage());
    }

    @Test
    public void shouldReturnExceptionWhenCreateUserAndRoleNotFound() {
        Optional<User> optUser = Optional.empty();

        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(optUser);

        Optional<Role> optRole = Optional.empty();

        Mockito.when(roleRepository.findByName(Mockito.any())).thenReturn(optRole);

        User user = new User();
        user.setUsername("test-user");
        user.setPassword("test-pass-user");

        Role roleAdmin = new Role();
        roleAdmin.setName("ROLE_ADMIN");

        Set<Role> roles = Set.of(roleAdmin);

        user.setRoles(roles);

        ResourceNotFoundException exception = Assertions.catchThrowableOfType(ResourceNotFoundException.class,
                () -> userService.create(user));

        Mockito.verify(userRepository, Mockito.atMostOnce()).findByUsername(Mockito.any());
        Mockito.verify(roleRepository, Mockito.atMostOnce()).findByName(Mockito.any());
        Mockito.verify(passwordEncoder, Mockito.never()).encode(Mockito.any());
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());

        Assertions.assertThat(exception.getCode()).isEqualTo(OrderApiError.ROLE_NOT_FOUND.getCode());
        Assertions.assertThat(exception.getFormattedMessage()).isEqualTo(OrderApiError.ROLE_NOT_FOUND.getMessage().formatted(roleAdmin.getName()));
    }

}