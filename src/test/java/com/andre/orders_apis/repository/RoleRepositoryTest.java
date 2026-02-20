package com.andre.orders_apis.repository;

import com.andre.orders_apis.entity.Role;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    public void beforeEach() {
        Role roleAdmin = new Role();
        roleAdmin.setName("ROLE_ADMIN_TEST");

        Role createdRoleAdminTest = roleRepository.save(roleAdmin);
        Assertions.assertThat(createdRoleAdminTest.getId()).isGreaterThan(0);
    }

    @Test
    public void shouldCreateRoleSuccessfully() {
        Role roleAdmin = new Role();
        roleAdmin.setName("ROLE_ADMIN");

        Role createdRoleAdmin = roleRepository.save(roleAdmin);

        Assertions.assertThat(createdRoleAdmin.getId()).isGreaterThan(0);
        Assertions.assertThat(createdRoleAdmin.getName()).isEqualTo(roleAdmin.getName());
    }

    @Test
    public void shouldNotCreateRoleWhenAlreadyExists() {
        Role roleAdminTest = new Role();
        roleAdminTest.setName("ROLE_ADMIN_TEST");

        Assertions.assertThatThrownBy(() -> roleRepository.save(roleAdminTest))
                .isExactlyInstanceOf(DataIntegrityViolationException.class);
    }

}