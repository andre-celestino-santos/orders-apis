package com.andre.orders_apis.repository;

import com.andre.orders_apis.entity.Role;
import com.andre.orders_apis.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@DataJpaTest
@Sql(scripts = "classpath:auth.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Role adminRole;

    @BeforeEach
    public void beforeEach() {
        if (adminRole == null) {
            adminRole = roleRepository.findByName("ROLE_ADMIN").orElseThrow();
        }
    }

    @Test
    public void shouldCreateUserSuccessfully() {
        User user = new User();

        user.setUsername("create-user-test-username");
        user.setPassword("create-user-test-password");

        User createdUser = userRepository.save(user);

        Assertions.assertThat(createdUser.getId()).isGreaterThan(0);
        Assertions.assertThat(createdUser.getUsername()).isEqualTo(user.getUsername());
        Assertions.assertThat(createdUser.getPassword()).isEqualTo(user.getPassword());
        Assertions.assertThat(createdUser.getRoles()).isEmpty();
    }

    @Test
    public void shouldCreateUserRolesWhenCreateUserSuccessfully() {
        User user = new User();

        user.setUsername("create-user-test-username");
        user.setPassword("create-user-test-password");

        Set<Role> roles = Set.of(adminRole);

        user.setRoles(roles);

        User createdUser = userRepository.save(user);

        Assertions.assertThat(createdUser.getId()).isGreaterThan(0);
        Assertions.assertThat(createdUser.getUsername()).isEqualTo(user.getUsername());
        Assertions.assertThat(createdUser.getPassword()).isEqualTo(user.getPassword());
        Assertions.assertThat(createdUser.getRoles()).isNotEmpty();
        createdUser.getRoles()
                .forEach(role -> Assertions.assertThat(role.getId()).isNotNull());
    }

    @Test
    public void shouldReturnUserWhenFindByUsernameSuccessfully() {
        Optional<User> optUser = userRepository.findByUsername("test-admin-user");

        Assertions.assertThat(optUser).isPresent();
    }

    @Test
    public void shouldReturnUserRolesWhenFindByUsernameSuccessfully() {
        Optional<User> optCreatedUser = userRepository.findByUsername("test-admin-user");

        Assertions.assertThat(optCreatedUser).isPresent();

        User createdUser = optCreatedUser.get();

        createdUser.getRoles()
                .forEach(role -> Assertions.assertThat(role.getId()).isNotNull());
    }

    @Test
    public void shouldDeleteUserRolesSuccessfully() {
        Optional<User> optCreatedUser = userRepository.findByUsername("test-user");

        Assertions.assertThat(optCreatedUser).isPresent();

        User createdUser = optCreatedUser.get();

        createdUser.setRoles(new HashSet<>());

        User updatedUser = userRepository.save(createdUser);

        Assertions.assertThat(updatedUser.getRoles()).isEmpty();
    }

    @Test
    public void shouldNotCreateUserWhenAlreadyExists() {
        User user = new User();

        user.setUsername("test-admin-user");
        user.setPassword("test-admin-user");

        Assertions.assertThatThrownBy(() -> userRepository.save(user))
                .isExactlyInstanceOf(DataIntegrityViolationException.class);
    }

}