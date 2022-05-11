package com.shopme.admin;

import com.shopme.admin.dao.RoleRepo;
import com.shopme.admin.entity.Role;
import com.shopme.admin.entity.Roles;
import com.shopme.admin.service.RoleService;
import com.shopme.admin.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest
@Rollback(value = false)
public class RoleServiceTest {

    @Autowired
    RoleRepo roleRepo;

    @Autowired
    RoleService roleService;

    @Autowired
    UserService userService;

    @Test
    public void testSaveAllRoles() {

        userService.saveRole(Roles.Admin.name(), "Manage everything");
        userService.saveRole(Roles.Salesperson.name(),
                "Manage product price, customers, shipping, orders and sales report");
        userService.saveRole(Roles.Editor.name(), "Manage categories, brands, products, articles and menus");
        userService.saveRole(Roles.Shipper.name(), "View products, view orders and update order status");
        userService.saveRole(Roles.Assistant.name(),
                "Manage product price, customers, shipping, orders and sales report");

        org.assertj.core.api.Assertions.assertThat(roleService.findAll()).size().isGreaterThan(4);
    }

    @Test
    public void testFindByName() {

        String roleName = "Salesperson";

        Role role = roleRepo.findByName(roleName);

        org.junit.jupiter.api.Assertions.assertEquals(roleName, role.getName());
    }

    @Test
    public void testFindAll() {

        List<Role> roles = roleService.findAll();

        Assertions.assertThat(roles).size().isGreaterThan(4);
    }

    @Test
    public void testFindOne() {

        int roleId = 5;

        Role role = roleService.findOne(roleId);

        org.junit.jupiter.api.Assertions.assertEquals(roleId, role.getId());
    }
}
