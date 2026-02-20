package com.andre.orders_apis.repository;

import com.andre.orders_apis.entity.Role;
import com.andre.orders_apis.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    public void beforeEach() {
        User user = new User();

        user.setUsername("test-user-username");
        user.setPassword("test-user-password");

        Role roleCreatedTest = new Role();
        roleCreatedTest.setName("ROLE_CREATED_TEST");

        Set<Role> roles = Set.of(roleCreatedTest);

        user.setRoles(roles);

        User createdUser = userRepository.save(user);
        Assertions.assertThat(createdUser.getId()).isGreaterThan(0);

        testUser = createdUser;
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

        Role roleNotCreated = new Role();
        roleNotCreated.setName("ROLE_NOT_CREATED");

        Set<Role> roles = Set.of(roleNotCreated);

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
        Optional<User> optUser = userRepository.findByUsername(testUser.getUsername());

        Assertions.assertThat(optUser).isPresent();
    }

    @Test
    public void shouldReturnUserRolesWhenFindByUsernameSuccessfully() {
        Optional<User> optCreatedUser = userRepository.findByUsername(testUser.getUsername());

        Assertions.assertThat(optCreatedUser).isPresent();

        User createdUser = optCreatedUser.get();

        createdUser.getRoles()
                .forEach(role -> Assertions.assertThat(role.getId()).isNotNull());
    }

    @Test
    public void shouldDeleteUserRolesSuccessfully() {
        Optional<User> optCreatedUser = userRepository.findByUsername(testUser.getUsername());

        Assertions.assertThat(optCreatedUser).isPresent();

        User createdUser = optCreatedUser.get();

        createdUser.setRoles(new HashSet<>());

        User updatedUser = userRepository.save(createdUser);

        Assertions.assertThat(updatedUser.getRoles()).isEmpty();
    }

    @Test
    public void shouldNotCreateUserWhenAlreadyExists() {
        User user = new User();

        user.setUsername(testUser.getUsername());
        user.setPassword(testUser.getPassword());

        Assertions.assertThatThrownBy(() -> userRepository.save(user))
                .isExactlyInstanceOf(DataIntegrityViolationException.class);
    }

}