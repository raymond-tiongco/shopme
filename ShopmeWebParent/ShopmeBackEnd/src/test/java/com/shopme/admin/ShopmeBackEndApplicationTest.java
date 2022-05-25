package com.shopme.admin;

import com.shopme.admin.entity.Roles;
import com.shopme.admin.entity.User;
import com.shopme.admin.service.RoleService;
import com.shopme.admin.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
class ShopmeBackEndApplicationTest {

    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    @Test
    @Rollback(value = false)
    public void testSaveAllRoles() {

        userService.saveRole(Roles.Admin.name(), Roles.Admin.DESCRIPTION);
        userService.saveRole(Roles.Salesperson.name(), Roles.Salesperson.DESCRIPTION);
        userService.saveRole(Roles.Editor.name(), Roles.Editor.DESCRIPTION);
        userService.saveRole(Roles.Shipper.name(), Roles.Shipper.DESCRIPTION);
        userService.saveRole(Roles.Assistant.name(), Roles.Assistant.DESCRIPTION);

        org.assertj.core.api.Assertions.assertThat(roleService.findAll()).size().isGreaterThan(4);
    }

    @Test
    @Rollback(value = false)
    public void saveRootUserTest() {

        String newEmail = "darylldagondon@gmail.com";

        User root = new User()
                .email(newEmail)
                .enabled(1)
                .firstName("Daryll David")
                .lastName("Dagondon")
                .password("daryll123");

        userService.saveUser(root);

        userService.addRoleToUser(newEmail, Roles.Admin.name());

        User user = userService.findByEmail(newEmail);

        org.junit.jupiter.api.Assertions.assertEquals(newEmail, user.getEmail());
    }

    @Test
    @Rollback(value = false)
    public void testEnable() {

        int id = 4;

        User user = userService.findById(4);
        user.enable();

        org.junit.jupiter.api.Assertions.assertEquals(1, user.getEnabled());
    }

    @Test
    @Rollback(value = false)
    public void testDisable() {

        int id = 4;

        User user = userService.findById(4);
        user.disable();

        System.out.println(user);

        org.junit.jupiter.api.Assertions.assertEquals(0, user.getEnabled());
    }
}