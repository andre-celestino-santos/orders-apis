package com.andre.orders_apis.repository;

import com.andre.orders_apis.entity.Role;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

@DataJpaTest
@Sql(scripts = "classpath:auth.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void shouldCreateRoleSuccessfully() {
        Role roleAdmin = new Role();
        roleAdmin.setName("ROLE_TEST_ADMIN");

        Role createdRoleAdmin = roleRepository.save(roleAdmin);

        Assertions.assertThat(createdRoleAdmin.getId()).isGreaterThan(0);
        Assertions.assertThat(createdRoleAdmin.getName()).isEqualTo(roleAdmin.getName());
    }

    @Test
    public void shouldReturnRoleByNameSuccessfully() {
        Optional<Role> optRole = roleRepository.findByName("ROLE_USER");

        Assertions.assertThat(optRole).isPresent();
    }

    @Test
    public void shouldNotReturnRoleByNameSuccessfully() {
        Optional<Role> optRole = roleRepository.findByName("ROLE_NOT_FOUND");

        Assertions.assertThat(optRole).isEmpty();
    }

    @Test
    public void shouldNotCreateRoleWhenAlreadyExists() {
        Role roleAdminTest = new Role();
        roleAdminTest.setName("ROLE_ADMIN");

        Assertions.assertThatThrownBy(() -> roleRepository.save(roleAdminTest))
                .isExactlyInstanceOf(DataIntegrityViolationException.class);
    }

}